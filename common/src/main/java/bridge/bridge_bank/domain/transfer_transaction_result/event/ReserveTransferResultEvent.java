package bridge.bridge_bank.domain.transfer_transaction_result.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record ReserveTransferResultEvent(
        String transferTransactionGroupId,
        String resultStatus,
        String transferTransactionType,
        BigDecimal transferAmount,
        String senderAccountNumber,
        String receiverAccountNumber,
        LocalDateTime transferTransactionDate,
        String failReason
) {
    public static ReserveTransferResultEvent success(
            String transferTransactionGroupId,
            String transferTransactionType,
            BigDecimal transferAmount,
            String senderAccountNumber,
            String receiverAccountNumber,
            LocalDateTime transferTransactionDate
    ) {
        return new ReserveTransferResultEvent(
                transferTransactionGroupId, "SUCCESS", transferTransactionType,
                transferAmount, senderAccountNumber, receiverAccountNumber,
                transferTransactionDate, null
        );
    }

    public static ReserveTransferResultEvent fail(
            String transferTransactionType,
            BigDecimal transferAmount,
            String senderAccountNumber,
            String receiverAccountNumber,
            String failReason
    ) {
        return new ReserveTransferResultEvent(
                null, "FAIL", transferTransactionType,
                transferAmount, senderAccountNumber, receiverAccountNumber,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")), failReason
        );
    }
}
