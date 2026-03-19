package bridge.bridge_bank.domain.transfer.dto;

import bridge.bridge_bank.domain.transfer.entity.TransferTransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TransferTransactionResultTargetOption {
    private String receiverAccountNumber;
    private TransferTransactionType transferTransactionType;
}
