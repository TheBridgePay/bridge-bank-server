package bridge.bridge_bank.api.dto;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReserveRepeatScheduleResponse {
    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal transferAmount;
    private LocalDateTime transferDateTime;
    private String repeatType;
    private Integer repeatValue;

    public static ReserveRepeatScheduleResponse from(ReserveRepeatTransferSchedule schedule) {
        return new ReserveRepeatScheduleResponse(
                schedule.getId(),
                schedule.getSenderAccountNumber(),
                schedule.getReceiverAccountNumber(),
                schedule.getTransferAmount(),
                schedule.getTransferDateTime(),
                schedule.getRepeatType().name(),
                schedule.getRepeatValue()
        );
    }
}
