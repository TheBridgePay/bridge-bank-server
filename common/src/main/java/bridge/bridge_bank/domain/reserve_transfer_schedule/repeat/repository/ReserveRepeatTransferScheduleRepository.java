package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReserveRepeatTransferScheduleRepository extends JpaRepository<ReserveRepeatTransferSchedule, Long> {

    @Modifying
    @Query("update ReserveRepeatTransferSchedule r " +
            "set r.transferDateTime=:newTransferDateTime where r.id=:id")
    void updateTransferDateTimeById(@Param("id") Long id, @Param("newTransferDateTime") LocalDateTime newTransferDateTime);
}
