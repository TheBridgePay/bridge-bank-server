package bridge.bridge_bank.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferResponse {
    private String transferTransactionGroupId;
    private BigDecimal transferAmount;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private LocalDateTime transferTransactionDate;

    public static TransferResponse of(
            String groupId, BigDecimal amount,
            String sender, String receiver, LocalDateTime date
    ) {
        return new TransferResponse(groupId, amount, sender, receiver, date);
    }
}
