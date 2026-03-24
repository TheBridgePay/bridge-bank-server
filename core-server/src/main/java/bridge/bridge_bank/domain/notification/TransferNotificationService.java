package bridge.bridge_bank.domain.notification;

import bridge.bridge_bank.domain.notification.entity.TransferNotification;
import bridge.bridge_bank.domain.notification.entity.TransferNotificationStatus;
import bridge.bridge_bank.global.error.AccessDeniedException;
import bridge.bridge_bank.global.error.EntityNotFoundException;
import bridge.bridge_bank.infra.kafka.AccountNotificationEvent;
import bridge.bridge_bank.infra.kafka.AccountNotificationEventPublisher;
import bridge.bridge_bank.domain.notification.repository.TransferNotificationRepository;
import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferNotificationService {

    private final TransferNotificationRepository transferNotificationRepository;
    private final AccountNotificationEventPublisher accountNotificationEventPublisher;

    @Transactional
    public void createNotificationsFromTransferResult(ReserveTransferResultEvent event) {
        boolean isSuccess = "SUCCESS".equals(event.resultStatus());

        // 송금자 알림 (성공/실패 모두)
        TransferNotification senderNotification = TransferNotification.createForSender(
                event.transferTransactionGroupId(),
                event.senderAccountNumber(),
                event.receiverAccountNumber(),
                event.transferAmount(),
                isSuccess,
                event.failReason()
        );

        // 수취자 알림 (성공 시에만)
        TransferNotification receiverNotification = null;
        if (isSuccess) {
            receiverNotification = TransferNotification.createForReceiver(
                    event.transferTransactionGroupId(),
                    event.senderAccountNumber(),
                    event.receiverAccountNumber(),
                    event.transferAmount()
            );
        }

        // DB 저장
        transferNotificationRepository.save(senderNotification);
        if (receiverNotification != null) {
            transferNotificationRepository.save(receiverNotification);
        }

        // Kafka 발행 (Bridge Pay용 실시간 이벤트)
        publishNotificationEvent(senderNotification);
        if (receiverNotification != null) {
            publishNotificationEvent(receiverNotification);
        }
    }

    @Transactional(readOnly = true)
    public Page<TransferNotification> getNotifications(String accountNumber, Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return transferNotificationRepository.findByAccountNumber(accountNumber, sorted);
    }

    @Transactional(readOnly = true)
    public Page<TransferNotification> getUnreadNotifications(String accountNumber, Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return transferNotificationRepository.findByAccountNumberAndStatus(
                accountNumber, TransferNotificationStatus.UNREAD, sorted);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String accountNumber) {
        return transferNotificationRepository
                .countByAccountNumberAndStatus(accountNumber, TransferNotificationStatus.UNREAD);
    }

    @Transactional
    public void markAsRead(Long notificationId, String accountNumber) {
        TransferNotification notification = transferNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found: " + notificationId));

        if (!notification.getAccountNumber().equals(accountNumber)) {
            throw new AccessDeniedException(
                    "Notification does not belong to account: " + accountNumber);
        }

        notification.markAsRead();
    }

    @Transactional
    public int markAllAsRead(String accountNumber) {
        return transferNotificationRepository.markAllAsRead(accountNumber);
    }

    private void publishNotificationEvent(TransferNotification notification) {
        AccountNotificationEvent event = new AccountNotificationEvent(
                notification.getId(),
                notification.getAccountNumber(),
                notification.getNotificationType().name(),
                notification.getTransferAmount(),
                notification.getSenderAccountNumber(),
                notification.getReceiverAccountNumber(),
                notification.getTransferTransactionGroupId(),
                notification.getFailReason(),
                notification.getCreatedAt()
        );
        accountNotificationEventPublisher.publish(event);
    }
}
