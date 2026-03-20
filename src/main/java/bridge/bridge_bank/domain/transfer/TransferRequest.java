package bridge.bridge_bank.domain.transfer;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    private String senderAccount;

    private String senderPassword;

    private String receiverAccount;

    private BigDecimal transferAmount;
}
