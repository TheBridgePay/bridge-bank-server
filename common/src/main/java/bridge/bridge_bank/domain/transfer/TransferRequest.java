package bridge.bridge_bank.domain.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferRequest {

    @NotBlank(message = "senderAccount is required")
    private String senderAccount;

    @NotBlank(message = "senderPassword is required")
    private String senderPassword;

    @NotBlank(message = "receiverAccount is required")
    private String receiverAccount;

    @NotNull(message = "transferAmount is required")
    @Positive(message = "transferAmount must be positive")
    private BigDecimal transferAmount;

    public static TransferRequest create(
            String senderAccount,
            String senderPassword,
            String receiverAccount,
            BigDecimal transferAmount
    ) {
        return TransferRequest.builder()
                .senderAccount(senderAccount)
                .senderPassword(senderPassword)
                .receiverAccount(receiverAccount)
                .transferAmount(transferAmount)
                .build();
    }
}
