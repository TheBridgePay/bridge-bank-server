package bridge.bridge_bank.domain.transfer_transaction_result;

import bridge.bridge_bank.domain.notification.TransferNotificationService;
import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReserveTransferResultEventConsumerTest {

    @Mock
    private TransferNotificationService transferNotificationService;

    @InjectMocks
    private ReserveTransferResultEventConsumer consumer;

    @Test
    @DisplayName("성공 이벤트 수신 시 TransferNotificationService 호출")
    void consume_successEvent_callsService() {
        // given
        ReserveTransferResultEvent event = ReserveTransferResultEvent.success(
                "group-123", "ONCE", BigDecimal.valueOf(50000),
                "sender-acc", "receiver-acc", LocalDateTime.now()
        );

        // when
        consumer.consume(event);

        // then
        verify(transferNotificationService).createNotificationsFromTransferResult(event);
    }

    @Test
    @DisplayName("실패 이벤트 수신 시 TransferNotificationService 호출")
    void consume_failEvent_callsService() {
        // given
        ReserveTransferResultEvent event = ReserveTransferResultEvent.fail(
                "ONCE", BigDecimal.valueOf(50000),
                "sender-acc", "receiver-acc", "잔액 부족"
        );

        // when
        consumer.consume(event);

        // then
        verify(transferNotificationService).createNotificationsFromTransferResult(event);
    }
}
