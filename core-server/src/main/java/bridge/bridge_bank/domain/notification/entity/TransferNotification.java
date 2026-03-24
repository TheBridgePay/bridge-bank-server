package bridge.bridge_bank.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(indexes = {
        @Index(name = "idx_notification_account_status_created",
                columnList = "accountNumber, status, createdAt DESC"),
        @Index(name = "idx_notification_account_created",
                columnList = "accountNumber, createdAt DESC")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TransferNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_notification_id")
    private Long id;

    private String accountNumber;

    private String transferTransactionGroupId;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private BigDecimal transferAmount;
    private String senderAccountNumber;
    private String receiverAccountNumber;

    @Enumerated(EnumType.STRING)
    private TransferNotificationStatus status;

    private String failReason;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public static TransferNotification createForSender(
            String transferTransactionGroupId,
            String senderAccountNumber,
            String receiverAccountNumber,
            BigDecimal transferAmount,
            boolean isSuccess,
            String failReason
    ) {
        return TransferNotification.builder()
                .accountNumber(senderAccountNumber)
                .transferTransactionGroupId(transferTransactionGroupId)
                .notificationType(isSuccess
                        ? NotificationType.TRANSFER_SENT
                        : NotificationType.TRANSFER_FAILED)
                .transferAmount(transferAmount)
                .senderAccountNumber(senderAccountNumber)
                .receiverAccountNumber(receiverAccountNumber)
                .status(TransferNotificationStatus.UNREAD)
                .failReason(failReason)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

    public static TransferNotification createForReceiver(
            String transferTransactionGroupId,
            String senderAccountNumber,
            String receiverAccountNumber,
            BigDecimal transferAmount
    ) {
        return TransferNotification.builder()
                .accountNumber(receiverAccountNumber)
                .transferTransactionGroupId(transferTransactionGroupId)
                .notificationType(NotificationType.TRANSFER_RECEIVED)
                .transferAmount(transferAmount)
                .senderAccountNumber(senderAccountNumber)
                .receiverAccountNumber(receiverAccountNumber)
                .status(TransferNotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

    public void markAsRead() {
        this.status = TransferNotificationStatus.READ;
        this.readAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
