package bridge.bridge_bank.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNotificationEventPublisher {

    private static final String TOPIC = "account-notification";
    private final KafkaTemplate<String, AccountNotificationEvent> kafkaTemplate;

    public void publish(AccountNotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.accountNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("알림 이벤트 발행 실패 - account: {}, type: {}",
                                event.accountNumber(), event.notificationType(), ex);
                    } else {
                        log.info("알림 이벤트 발행 성공 - account: {}, type: {}, notificationId: {}",
                                event.accountNumber(), event.notificationType(),
                                event.notificationId());
                    }
                });
    }
}
