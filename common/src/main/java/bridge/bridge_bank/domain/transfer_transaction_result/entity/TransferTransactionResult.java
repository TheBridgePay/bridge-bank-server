package bridge.bridge_bank.domain.transfer_transaction_result.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(indexes = {
        @Index(name = "idx_ttr_self_account_date",
                columnList = "selfAccountNumber, transferTransactionDate DESC"),
        @Index(name = "idx_ttr_type_date",
                columnList = "transferTransactionType, transferTransactionDate")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class TransferTransactionResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_transaction_result_id")
    private Long id;

    private LocalDateTime transferTransactionDate;

    private String transferTransactionGroupId;

    @Enumerated(EnumType.STRING)
    private TransferTransactionResultStatus transferTransactionResultStatus;

    @Enumerated(EnumType.STRING)
    private TransferTransactionType transferTransactionType;

    private BigDecimal transferAmount;

    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private String selfAccountNumber;

    private String otherAccountNumber;

    public static TransferTransactionResult create(
            String transferTransactionGroupId,
            TransferTransactionResultStatus transferTransactionResultStatus,
            TransferTransactionType transferTransactionType,
            BigDecimal transferAmount,
            BigDecimal beforeBalance,
            BigDecimal afterBalance,
            String selfAccountNumber,
            String otherAccountNumber
    ){
        return TransferTransactionResult.builder()
                .transferTransactionDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .transferTransactionGroupId(transferTransactionGroupId)
                .transferTransactionResultStatus(transferTransactionResultStatus)
                .transferTransactionType(transferTransactionType)
                .transferAmount(transferAmount)
                .beforeBalance(beforeBalance)
                .afterBalance(afterBalance)
                .selfAccountNumber(selfAccountNumber)
                .otherAccountNumber(otherAccountNumber)
                .build();
    }
}
