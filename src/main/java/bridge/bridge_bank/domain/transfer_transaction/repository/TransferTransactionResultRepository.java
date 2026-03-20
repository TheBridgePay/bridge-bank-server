package bridge.bridge_bank.domain.transfer_transaction.repository;

import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionResultRepository extends JpaRepository<TransferTransactionResult, Long> {
}
