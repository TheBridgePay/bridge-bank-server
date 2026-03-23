package bridge.bridge_bank.api.dto;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferTransactionResultResponse {
    private Long id;
    private LocalDateTime transferTransactionDate;
    private String transferTransactionGroupId;
    private String transferTransactionResultStatus;
    private String transferTransactionType;
    private BigDecimal transferAmount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private String selfAccountNumber;
    private String otherAccountNumber;

    public static TransferTransactionResultResponse from(TransferTransactionResult result) {
        return new TransferTransactionResultResponse(
                result.getId(),
                result.getTransferTransactionDate(),
                result.getTransferTransactionGroupId(),
                result.getTransferTransactionResultStatus().name(),
                result.getTransferTransactionType().name(),
                result.getTransferAmount(),
                result.getBeforeBalance(),
                result.getAfterBalance(),
                result.getSelfAccountNumber(),
                result.getOtherAccountNumber()
        );
    }
}
