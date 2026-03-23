package bridge.bridge_bank.domain.notification.event;

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
