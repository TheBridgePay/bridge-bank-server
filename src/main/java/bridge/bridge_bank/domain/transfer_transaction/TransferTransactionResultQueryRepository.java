package bridge.bridge_bank.domain.transfer_transaction;

import bridge.bridge_bank.domain.transfer_transaction.entity.QTransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TransferTransactionResultQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QTransferTransactionResult qTransferTransactionResult
            = QTransferTransactionResult.transferTransactionResult;

    public BigDecimal getYesterdaySumByTransferType(
            TransferTransactionType transferTransactionType
    ) {
        LocalDate today = LocalDate.now();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
        LocalDateTime todayStart = today.atStartOfDay();

        return queryFactory
                .select(
                        qTransferTransactionResult.transferAmount.sumBigDecimal().coalesce(BigDecimal.ZERO)
                ).from(qTransferTransactionResult)
                .where(
                        qTransferTransactionResult.transferTransactionDate.goe(yesterdayStart),
                        qTransferTransactionResult.transferTransactionDate.lt(todayStart),
                        qTransferTransactionResult.transferTransactionType.eq(transferTransactionType)
                ).fetchOne();
    }
}
