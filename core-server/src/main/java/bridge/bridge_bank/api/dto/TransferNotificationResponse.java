package bridge.bridge_bank.api.dto;

import bridge.bridge_bank.domain.notification.entity.TransferNotification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferNotificationResponse {
    private Long id;
    private String accountNumber;
    private String notificationType;
    private BigDecimal transferAmount;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String status;
    private String failReason;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public static TransferNotificationResponse from(TransferNotification notification) {
        return new TransferNotificationResponse(
                notification.getId(),
                notification.getAccountNumber(),
                notification.getNotificationType().name(),
                notification.getTransferAmount(),
                notification.getSenderAccountNumber(),
                notification.getReceiverAccountNumber(),
                notification.getStatus().name(),
                notification.getFailReason(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
