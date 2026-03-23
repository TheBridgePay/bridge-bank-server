package bridge.bridge_bank.infra.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountNotificationEvent(
        Long notificationId,
        String accountNumber,
        String notificationType,
        BigDecimal transferAmount,
        String senderAccountNumber,
        String receiverAccountNumber,
        String transferTransactionGroupId,
        String failReason,
        LocalDateTime createdAt
) {
}
