package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveRepeatTransferScheduleCreateRequest {
    private String senderAccount;

    private String senderPassword;

    private String receiverAccount;

    private BigDecimal transferAmount;

    private LocalDateTime transferDateTime;

    private RepeatType repeatType;

    private Integer repeatValue;
}
