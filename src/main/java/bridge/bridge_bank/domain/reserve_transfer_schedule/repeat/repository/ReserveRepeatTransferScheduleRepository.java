package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveRepeatTransferScheduleRepository extends JpaRepository<ReserveRepeatTransferSchedule, Long> {

}
