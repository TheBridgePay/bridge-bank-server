package bridge.bridge_bank.domain.ledger.entity;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class LedgerVoucher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_voucher_id")
    private Long id;

    private LocalDateTime ledgerVoucherDate;

    private String transferTransactionGroupId;

    @Enumerated(EnumType.STRING)
    private TransferTransactionType transferTransactionType;

    /*@OneToMany(mappedBy = "ledgerVoucher",fetch = FetchType.LAZY)
    @BatchSize(size=400)
    private List<LedgerEntry> ledgerEntries;*/

    public static LedgerVoucher create(
            String transferTransactionGroupId,
            TransferTransactionType transferTransactionType
    ) {
        return LedgerVoucher.builder()
                .ledgerVoucherDate(LocalDateTime.now())
                .transferTransactionGroupId(transferTransactionGroupId)
                .transferTransactionType(transferTransactionType)
                .build();
    }
}
