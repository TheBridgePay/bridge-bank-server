package bridge.bridge_bank.domain.reserve_transfer_schedule.once;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveOnceTransferScheduleService {
    private final ReserveOnceTransferScheduleRepository reserveOnceTransferScheduleRepository;
    private final ReserveOnceTransferScheduleQueryRepository reserveOnceTransferScheduleQueryRepository;

    @Transactional
    public void insertReserveOnceTransferSchedules(
            List<ReserveOnceTransferSchedule> reserveOnceTransferSchedules
    ) {
        reserveOnceTransferScheduleRepository.saveAll(reserveOnceTransferSchedules);
    }

    @Transactional
    public List<ReserveOnceTransferSchedule> getReserveOnceTransferSchedules(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ) {
        return reserveOnceTransferScheduleQueryRepository.getReserveOnceTransferSchedules(
                senderAccountNumber, reserveOnceTransferScheduleTargetOption
        );
    }

    @Transactional
    public void deleteReserveOnceTransferSchedules(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ){
        reserveOnceTransferScheduleQueryRepository.deleteReserveOnceTransferSchedule(
                senderAccountNumber, reserveOnceTransferScheduleTargetOption
        );
    }
}
