package bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveOnceTransferScheduleRepository extends JpaRepository<ReserveOnceTransferSchedule, Long> {
}
