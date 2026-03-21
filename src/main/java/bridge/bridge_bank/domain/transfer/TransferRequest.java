package bridge.bridge_bank.domain.transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferRequest {

    private String senderAccount;

    private String senderPassword;

    private String receiverAccount;

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
