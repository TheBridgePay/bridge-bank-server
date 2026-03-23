package bridge.bridge_bank.domain.transfer_transaction_result;

import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveTransferResultEventConsumer {

    @KafkaListener(
            topics = "reserve-transfer-result",
            groupId = "bridge-bank-core-server"
    )
    public void consume(ReserveTransferResultEvent event) {
        log.info("예약 이체 결과 이벤트 수신 - groupId: {}, status: {}, sender: {}, receiver: {}",
                event.transferTransactionGroupId(),
                event.resultStatus(),
                event.senderAccountNumber(),
                event.receiverAccountNumber());
    }
}
