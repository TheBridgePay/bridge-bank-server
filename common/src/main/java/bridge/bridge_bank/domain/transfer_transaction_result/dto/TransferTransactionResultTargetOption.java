package bridge.bridge_bank.domain.transfer_transaction_result.dto;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferTransactionResultTargetOption {
    private String receiverAccountNumber;
    private TransferTransactionType transferTransactionType;
}
