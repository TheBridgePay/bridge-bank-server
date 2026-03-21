package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ReserveRepeatTransferScheduleRepository extends JpaRepository<ReserveRepeatTransferSchedule, Long> {

    @Modifying
    @Query("update ReserveRepeatTransferSchedule r " +
            "set r.transferDateTime=:newTransferDateTime where r.id=:id")
    void updateTransferDateTimeById(Long id, LocalDateTime newTransferDateTime);
}
