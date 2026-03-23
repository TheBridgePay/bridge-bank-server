package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveTransferResultEventPublisher {

    private static final String TOPIC = "reserve-transfer-result";

    private final KafkaTemplate<String, ReserveTransferResultEvent> kafkaTemplate;

    public void publish(ReserveTransferResultEvent event) {
        kafkaTemplate.send(TOPIC, event.senderAccountNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 이벤트 발행 실패 - sender: {}, status: {}",
                                event.senderAccountNumber(), event.resultStatus(), ex);
                    } else {
                        log.info("Kafka 이벤트 발행 성공 - groupId: {}, sender: {}, status: {}",
                                event.transferTransactionGroupId(),
                                event.senderAccountNumber(),
                                event.resultStatus());
                    }
                });
    }
}
