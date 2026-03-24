package bridge.bridge_bank.domain.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(indexes = {
        @Index(name = "idx_le_voucher_type",
                columnList = "ledgerVoucherId, ledgerEntryType")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class LedgerEntry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_entry_id")
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private LedgerEntryType ledgerEntryType;

    @Enumerated(EnumType.STRING)
    private LedgerBankAssetType ledgerBankAssetType;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ledger_voucher_id")
    private LedgerVoucher ledgerVoucher;

    public void bindWithLedgerVoucher(LedgerVoucher ledgerVoucher) {
        this.ledgerVoucher = ledgerVoucher;
        ledgerVoucher.getLedgerEntries().add(this);
    }*/

    private Long ledgerVoucherId;

    public static LedgerEntry create(
            BigDecimal amount,
            LedgerEntryType ledgerEntryType,
            LedgerBankAssetType ledgerBankAssetType,
            Long ledgerVoucherId
    ) {
        return LedgerEntry.builder()
                .amount(amount)
                .ledgerEntryType(ledgerEntryType)
                .ledgerBankAssetType(ledgerBankAssetType)
                .ledgerVoucherId(ledgerVoucherId)
                .build();
    }
}
