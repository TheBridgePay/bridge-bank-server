package bridge.bridge_bank.domain.transfer_transaction.dto;

import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
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
