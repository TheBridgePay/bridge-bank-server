package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveRepeatTransferScheduleService {
    private final ReserveRepeatTransferScheduleRepository reserveRepeatTransferScheduleRepository;
    private final ReserveRepeatTransferScheduleQueryRepository reserveRepeatTransferScheduleQueryRepository;

    @Transactional
    public void insertReserveRepeatTransferSchedules(
            List<ReserveRepeatTransferSchedule> reserveRepeatTransferSchedules
    ) {
        reserveRepeatTransferScheduleRepository.saveAll(reserveRepeatTransferSchedules);
    }

    @Transactional
    public List<ReserveRepeatTransferSchedule> getReserveRepeatTransferSchedules(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ) {
        return reserveRepeatTransferScheduleQueryRepository.getReserveRepeatTransferSchedules(
                senderAccountNumber, reserveRepeatTransferScheduleTargetOption
        );
    }

    @Transactional
    public void deleteReserveRepeatTransferSchedules(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ){
        reserveRepeatTransferScheduleQueryRepository.deleteReserveRepeatTransferSchedule(
                senderAccountNumber, reserveRepeatTransferScheduleTargetOption
        );
    }
}
