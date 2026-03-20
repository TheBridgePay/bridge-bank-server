package bridge.bridge_bank.domain.transfer_transaction.repository;

import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransferTransactionResultRepository extends JpaRepository<TransferTransactionResult, Long> {

}
