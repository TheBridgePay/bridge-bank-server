package bridge.bridge_bank.domain.transfer_transaction_result.repository;

import bridge.bridge_bank.domain.transfer_transaction_result.dto.TransferTransactionResultTargetOption;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.QTransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public List<TransferTransactionResult> get10TransferTransactionResults(
            String senderAccountNumber,
            TransferTransactionResultTargetOption option
    ) {
        return queryFactory
                .selectFrom(qTransferTransactionResult)
                .where(
                        qTransferTransactionResult.selfAccountNumber.eq(senderAccountNumber),
                        receiverAccountNumberEq(option.getReceiverAccountNumber()),
                        transferTypeEq(option.getTransferTransactionType())
                )
                .orderBy(qTransferTransactionResult.transferTransactionDate.desc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression receiverAccountNumberEq(String receiverAccountNumber) {
        return receiverAccountNumber != null
                ? qTransferTransactionResult.otherAccountNumber.eq(receiverAccountNumber)
                : null;
    }

    private BooleanExpression transferTypeEq(TransferTransactionType type) {
        return type != null ? qTransferTransactionResult.transferTransactionType.eq(type)
                : null;
    }
}
