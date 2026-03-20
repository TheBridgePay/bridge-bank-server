package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.QReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleTargetOption;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReserveRepeatTransferScheduleQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QReserveRepeatTransferSchedule qReserveRepeatTransferSchedule
            = QReserveRepeatTransferSchedule.reserveRepeatTransferSchedule;

    private BooleanExpression receiverAccountNumberEq(String receiverAccountNumberCond) {
        if (receiverAccountNumberCond == null || receiverAccountNumberCond.isEmpty()) {
            return null;
        }

        return qReserveRepeatTransferSchedule.receiverAccountNumber.eq(receiverAccountNumberCond);
    }

    public List<ReserveRepeatTransferSchedule> getReserveRepeatTransferSchedules(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ) {
        return queryFactory.select(qReserveRepeatTransferSchedule)
                .from(qReserveRepeatTransferSchedule)
                .where(
                        qReserveRepeatTransferSchedule.senderAccountNumber.eq(senderAccountNumber),
                        receiverAccountNumberEq(reserveRepeatTransferScheduleTargetOption.getReceiverAccountNumber())
                )
                .fetch();
    }

    public void deleteReserveRepeatTransferSchedule(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ){
        queryFactory.delete(qReserveRepeatTransferSchedule)
                .where(
                        qReserveRepeatTransferSchedule.senderAccountNumber.eq(senderAccountNumber),
                        receiverAccountNumberEq(reserveRepeatTransferScheduleTargetOption.getReceiverAccountNumber())
                )
                .execute();
    }
}
