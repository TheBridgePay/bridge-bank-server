package bridge.bridge_bank.domain.notification.repository;

import bridge.bridge_bank.domain.notification.entity.TransferNotification;
import bridge.bridge_bank.domain.notification.entity.TransferNotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferNotificationRepository
        extends JpaRepository<TransferNotification, Long> {

    Page<TransferNotification> findByAccountNumber(
            String accountNumber, Pageable pageable);

    Page<TransferNotification> findByAccountNumberAndStatus(
            String accountNumber, TransferNotificationStatus status, Pageable pageable);

    long countByAccountNumberAndStatus(
            String accountNumber, TransferNotificationStatus status);

    @Modifying
    @Query("UPDATE TransferNotification n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.accountNumber = :accountNumber AND n.status = 'UNREAD'")
    int markAllAsRead(@Param("accountNumber") String accountNumber);
}
