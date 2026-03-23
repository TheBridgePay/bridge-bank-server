package bridge.bridge_bank.domain.notification;

import bridge.bridge_bank.domain.notification.entity.NotificationType;
import bridge.bridge_bank.domain.notification.entity.TransferNotification;
import bridge.bridge_bank.domain.notification.entity.TransferNotificationStatus;
import bridge.bridge_bank.domain.notification.event.AccountNotificationEvent;
import bridge.bridge_bank.domain.notification.event.AccountNotificationEventPublisher;
import bridge.bridge_bank.domain.notification.repository.TransferNotificationRepository;
import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferNotificationServiceTest {

    @Mock
    private TransferNotificationRepository transferNotificationRepository;

    @Mock
    private AccountNotificationEventPublisher accountNotificationEventPublisher;

    @InjectMocks
    private TransferNotificationService transferNotificationService;

    @Captor
    private ArgumentCaptor<TransferNotification> notificationCaptor;

    @Captor
    private ArgumentCaptor<AccountNotificationEvent> eventCaptor;

    @Test
    @DisplayName("성공 이벤트 수신 시 송금자 + 수취자 알림 2건 생성 및 Kafka 발행")
    void successEvent_createsTwoNotifications() {
        // given
        ReserveTransferResultEvent event = ReserveTransferResultEvent.success(
                "group-123", "ONCE", BigDecimal.valueOf(50000),
                "sender-acc", "receiver-acc", LocalDateTime.now()
        );

        when(transferNotificationRepository.save(any(TransferNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        transferNotificationService.createNotificationsFromTransferResult(event);

        // then - DB 저장 2건
        verify(transferNotificationRepository, times(2)).save(notificationCaptor.capture());
        List<TransferNotification> saved = notificationCaptor.getAllValues();

        // 송금자 알림
        TransferNotification senderNotification = saved.get(0);
        assertThat(senderNotification.getAccountNumber()).isEqualTo("sender-acc");
        assertThat(senderNotification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_SENT);
        assertThat(senderNotification.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(senderNotification.getStatus()).isEqualTo(TransferNotificationStatus.UNREAD);
        assertThat(senderNotification.getFailReason()).isNull();

        // 수취자 알림
        TransferNotification receiverNotification = saved.get(1);
        assertThat(receiverNotification.getAccountNumber()).isEqualTo("receiver-acc");
        assertThat(receiverNotification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_RECEIVED);
        assertThat(receiverNotification.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(receiverNotification.getStatus()).isEqualTo(TransferNotificationStatus.UNREAD);

        // Kafka 발행 2건
        verify(accountNotificationEventPublisher, times(2)).publish(eventCaptor.capture());
        List<AccountNotificationEvent> events = eventCaptor.getAllValues();
        assertThat(events.get(0).accountNumber()).isEqualTo("sender-acc");
        assertThat(events.get(0).notificationType()).isEqualTo("TRANSFER_SENT");
        assertThat(events.get(1).accountNumber()).isEqualTo("receiver-acc");
        assertThat(events.get(1).notificationType()).isEqualTo("TRANSFER_RECEIVED");
    }

    @Test
    @DisplayName("실패 이벤트 수신 시 송금자 알림 1건만 생성")
    void failEvent_createsOnlyOneNotification() {
        // given
        ReserveTransferResultEvent event = ReserveTransferResultEvent.fail(
                "ONCE", BigDecimal.valueOf(50000),
                "sender-acc", "receiver-acc", "잔액 부족"
        );

        when(transferNotificationRepository.save(any(TransferNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        transferNotificationService.createNotificationsFromTransferResult(event);

        // then - DB 저장 1건 (송금자만)
        verify(transferNotificationRepository, times(1)).save(notificationCaptor.capture());
        TransferNotification senderNotification = notificationCaptor.getValue();
        assertThat(senderNotification.getAccountNumber()).isEqualTo("sender-acc");
        assertThat(senderNotification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_FAILED);
        assertThat(senderNotification.getFailReason()).isEqualTo("잔액 부족");
        assertThat(senderNotification.getTransferTransactionGroupId()).isNull();

        // Kafka 발행 1건
        verify(accountNotificationEventPublisher, times(1)).publish(eventCaptor.capture());
        AccountNotificationEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.accountNumber()).isEqualTo("sender-acc");
        assertThat(publishedEvent.notificationType()).isEqualTo("TRANSFER_FAILED");
        assertThat(publishedEvent.failReason()).isEqualTo("잔액 부족");
    }

    @Test
    @DisplayName("markAsRead - 정상적으로 알림을 읽음 처리")
    void markAsRead_success() {
        // given
        TransferNotification notification = TransferNotification.createForSender(
                "group-123", "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000), true, null
        );
        when(transferNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        // when
        transferNotificationService.markAsRead(1L, "sender-acc");

        // then
        assertThat(notification.getStatus()).isEqualTo(TransferNotificationStatus.READ);
        assertThat(notification.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("markAsRead - 존재하지 않는 알림이면 예외 발생")
    void markAsRead_notFound() {
        // given
        when(transferNotificationRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transferNotificationService.markAsRead(999L, "sender-acc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notification not found");
    }

    @Test
    @DisplayName("markAsRead - 다른 계좌의 알림이면 예외 발생")
    void markAsRead_wrongAccount() {
        // given
        TransferNotification notification = TransferNotification.createForSender(
                "group-123", "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000), true, null
        );
        when(transferNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        // when & then
        assertThatThrownBy(() -> transferNotificationService.markAsRead(1L, "other-acc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to account");
    }

    @Test
    @DisplayName("markAllAsRead - 리포지토리의 markAllAsRead 호출")
    void markAllAsRead() {
        // given
        when(transferNotificationRepository.markAllAsRead("sender-acc"))
                .thenReturn(3);

        // when
        int count = transferNotificationService.markAllAsRead("sender-acc");

        // then
        assertThat(count).isEqualTo(3);
        verify(transferNotificationRepository).markAllAsRead("sender-acc");
    }
}
