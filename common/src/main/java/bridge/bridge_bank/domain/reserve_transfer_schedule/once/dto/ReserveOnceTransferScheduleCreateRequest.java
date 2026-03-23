package bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveOnceTransferScheduleCreateRequest {
    @NotBlank(message = "senderAccount is required")
    private String senderAccount;

    @NotBlank(message = "senderPassword is required")
    private String senderPassword;

    @NotBlank(message = "receiverAccount is required")
    private String receiverAccount;

    @NotNull(message = "transferAmount is required")
    @Positive(message = "transferAmount must be positive")
    private BigDecimal transferAmount;

    @NotNull(message = "transferDateTime is required")
    @Future(message = "transferDateTime must be in the future")
    private LocalDateTime transferDateTime;
}
