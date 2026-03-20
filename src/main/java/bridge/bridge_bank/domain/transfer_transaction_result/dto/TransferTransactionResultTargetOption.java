package bridge.bridge_bank.domain.transfer_transaction_result.dto;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
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
