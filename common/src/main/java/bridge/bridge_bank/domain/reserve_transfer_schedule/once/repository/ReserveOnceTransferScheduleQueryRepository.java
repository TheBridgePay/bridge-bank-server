package bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.QReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleTargetOption;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReserveOnceTransferScheduleQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QReserveOnceTransferSchedule qreserveOnceTransferSchedule=
            QReserveOnceTransferSchedule.reserveOnceTransferSchedule;

    private BooleanExpression receiverAccountNumberEq(String receiverAccountNumberCond) {
        if (receiverAccountNumberCond == null || receiverAccountNumberCond.isEmpty()) {
            return null;
        }

        return qreserveOnceTransferSchedule.receiverAccountNumber.eq(receiverAccountNumberCond);
    }

    public List<ReserveOnceTransferSchedule> getPendingReserveOnceTransferSchedules(LocalDateTime now) {
        return queryFactory.selectFrom(qreserveOnceTransferSchedule)
                .where(qreserveOnceTransferSchedule.transferDateTime.lt(now))
                .fetch();
    }

    public List<ReserveOnceTransferSchedule> getReserveOnceTransferSchedules(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ) {
        return queryFactory.select(qreserveOnceTransferSchedule)
                .from(qreserveOnceTransferSchedule)
                .where(
                        qreserveOnceTransferSchedule.senderAccountNumber.eq(senderAccountNumber),
                        receiverAccountNumberEq(reserveOnceTransferScheduleTargetOption.getReceiverAccountNumber())
                )
                .fetch();
    }

    public void deleteReserveOnceTransferSchedule(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ){
        queryFactory.delete(qreserveOnceTransferSchedule)
                .where(
                        qreserveOnceTransferSchedule.senderAccountNumber.eq(senderAccountNumber),
                        receiverAccountNumberEq(reserveOnceTransferScheduleTargetOption.getReceiverAccountNumber())
                )
                .execute();
    }
}
