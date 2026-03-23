package bridge.bridge_bank.api.dto;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.entity.ReserveOnceTransferSchedule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReserveOnceScheduleResponse {
    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal transferAmount;
    private LocalDateTime transferDateTime;

    public static ReserveOnceScheduleResponse from(ReserveOnceTransferSchedule schedule) {
        return new ReserveOnceScheduleResponse(
                schedule.getId(),
                schedule.getSenderAccountNumber(),
                schedule.getReceiverAccountNumber(),
                schedule.getTransferAmount(),
                schedule.getTransferDateTime()
        );
    }
}
