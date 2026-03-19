package bridge.bridge_bank.domain.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ledger_voucher_id")
    private LedgerVoucher ledgerVoucher;

    @Enumerated(EnumType.STRING)
    private BankAssetType bankAssetType;
}
