package bridge.bridge_bank.domain.ledger.repository;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.entity.QLedgerEntry;
import bridge.bridge_bank.domain.ledger.entity.QLedgerVoucher;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class LedgerQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QLedgerVoucher qLedgerVoucher = QLedgerVoucher.ledgerVoucher;
    private final QLedgerEntry qLedgerEntry = QLedgerEntry.ledgerEntry;

    /*
    *조건1 어제자 렛저 바우처
    * 조건2 이체 트랜잭션 타입
    * 조건3 렛저 타입
    * select coalesce(sum(e.amount), 0)
from ledger_voucher v
join ledger_entry e on e.ledger_voucher_id = v.ledger_voucher_id
where v.ledger_voucher_date >= curdate() - interval 1 day
  and v.ledger_voucher_date < curdate()
  and v.transfer_transaction_type = 'SIMPLE_TRANSACTION'
  and e.ledger_entry_type = 'DEBIT';
    *
    *
    * */

    public BigDecimal getYesterdaySumByTransferTypeAndLedgerType(
            TransferTransactionType transferTransactionType,
            LedgerEntryType ledgerEntryType
    ) {
        LocalDate today = LocalDate.now();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
        LocalDateTime todayStart = today.atStartOfDay();

        return queryFactory
                .select(
                        qLedgerEntry.amount.sumBigDecimal().coalesce(BigDecimal.ZERO)
                )
                .from(qLedgerVoucher)
                .join(qLedgerEntry)
                .on(qLedgerVoucher.id.eq(qLedgerEntry.ledgerVoucherId))
                .where(
                        qLedgerVoucher.ledgerVoucherDate.goe(yesterdayStart),
                        qLedgerVoucher.ledgerVoucherDate.lt(todayStart),
                        qLedgerVoucher.transferTransactionType.eq(transferTransactionType),
                        qLedgerEntry.ledgerEntryType.eq(ledgerEntryType)
                )
                .fetchOne();
    }
}
