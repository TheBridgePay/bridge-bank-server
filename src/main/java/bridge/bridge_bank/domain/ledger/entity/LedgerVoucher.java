package bridge.bridge_bank.domain.ledger.entity;

import bridge.bridge_bank.domain.transfer.entity.TransferTransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

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

    private String transactionGroupId;

    @Enumerated(EnumType.STRING)
    private TransferTransactionType transferTransactionType;

    @OneToMany(mappedBy = "ledgerVoucher",fetch = FetchType.LAZY)
    @BatchSize(size=4)
    private List<LedgerEntry> ledgerEntries;
}
