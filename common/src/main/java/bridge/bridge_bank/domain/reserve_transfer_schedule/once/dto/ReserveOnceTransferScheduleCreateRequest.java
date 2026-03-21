package bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveOnceTransferScheduleCreateRequest {
    private String senderAccount;

    private String senderPassword;

    private String receiverAccount;

    private BigDecimal transferAmount;

    private LocalDateTime transferDateTime;
}
