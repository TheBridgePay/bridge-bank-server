package bridge.bridge_bank.domain.notification.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferNotificationTest {

    @Test
    @DisplayName("createForSender - 성공 시 TRANSFER_SENT 알림 생성")
    void createForSender_success() {
        // when
        TransferNotification notification = TransferNotification.createForSender(
                "group-123", "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000), true, null
        );

        // then
        assertThat(notification.getAccountNumber()).isEqualTo("sender-acc");
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_SENT);
        assertThat(notification.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(notification.getSenderAccountNumber()).isEqualTo("sender-acc");
        assertThat(notification.getReceiverAccountNumber()).isEqualTo("receiver-acc");
        assertThat(notification.getTransferTransactionGroupId()).isEqualTo("group-123");
        assertThat(notification.getStatus()).isEqualTo(TransferNotificationStatus.UNREAD);
        assertThat(notification.getFailReason()).isNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getReadAt()).isNull();
    }

    @Test
    @DisplayName("createForSender - 실패 시 TRANSFER_FAILED 알림 생성")
    void createForSender_fail() {
        // when
        TransferNotification notification = TransferNotification.createForSender(
                null, "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000), false, "잔액 부족"
        );

        // then
        assertThat(notification.getAccountNumber()).isEqualTo("sender-acc");
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_FAILED);
        assertThat(notification.getTransferTransactionGroupId()).isNull();
        assertThat(notification.getStatus()).isEqualTo(TransferNotificationStatus.UNREAD);
        assertThat(notification.getFailReason()).isEqualTo("잔액 부족");
    }

    @Test
    @DisplayName("createForReceiver - 수취자 TRANSFER_RECEIVED 알림 생성")
    void createForReceiver() {
        // when
        TransferNotification notification = TransferNotification.createForReceiver(
                "group-123", "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000)
        );

        // then
        assertThat(notification.getAccountNumber()).isEqualTo("receiver-acc");
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.TRANSFER_RECEIVED);
        assertThat(notification.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(notification.getSenderAccountNumber()).isEqualTo("sender-acc");
        assertThat(notification.getReceiverAccountNumber()).isEqualTo("receiver-acc");
        assertThat(notification.getTransferTransactionGroupId()).isEqualTo("group-123");
        assertThat(notification.getStatus()).isEqualTo(TransferNotificationStatus.UNREAD);
        assertThat(notification.getFailReason()).isNull();
    }

    @Test
    @DisplayName("markAsRead - 상태가 READ로 변경되고 readAt이 설정됨")
    void markAsRead() {
        // given
        TransferNotification notification = TransferNotification.createForSender(
                "group-123", "sender-acc", "receiver-acc",
                BigDecimal.valueOf(50000), true, null
        );

        // when
        notification.markAsRead();

        // then
        assertThat(notification.getStatus()).isEqualTo(TransferNotificationStatus.READ);
        assertThat(notification.getReadAt()).isNotNull();
    }
}
