package bridge.bridge_bank.domain.transfer;

import bridge.bridge_bank.domain.transfer.entity.TransferTransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransferTransactionResultRepository extends JpaRepository<TransferTransactionResult, Long> {
}
