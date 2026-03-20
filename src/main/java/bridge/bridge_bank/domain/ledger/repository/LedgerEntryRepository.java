package bridge.bridge_bank.domain.ledger.repository;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}
