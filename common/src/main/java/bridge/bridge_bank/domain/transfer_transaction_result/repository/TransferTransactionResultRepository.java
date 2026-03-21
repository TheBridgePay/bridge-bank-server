package bridge.bridge_bank.domain.transfer_transaction_result.repository;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionResultRepository extends JpaRepository<TransferTransactionResult, Long> {

}
