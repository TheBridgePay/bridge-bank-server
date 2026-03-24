package bridge.bridge_bank.domain.ledger.repository;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntry;
import bridge.bridge_bank.domain.ledger.entity.LedgerVoucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LedgerBulkInsertMapper {

    void bulkInsertVouchers(@Param("vouchers") List<LedgerVoucher> vouchers);

    void bulkInsertEntries(@Param("entries") List<LedgerEntry> entries);
}
