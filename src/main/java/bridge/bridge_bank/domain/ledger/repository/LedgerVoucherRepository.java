package bridge.bridge_bank.domain.ledger.repository;

import bridge.bridge_bank.domain.ledger.entity.LedgerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LedgerVoucherRepository extends JpaRepository<LedgerVoucher, Long> {

}
