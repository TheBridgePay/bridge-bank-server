package bridge.bridge_bank.domain.ledger;

import bridge.bridge_bank.domain.ledger.entity.LedgerBankAssetType;
import bridge.bridge_bank.domain.ledger.entity.LedgerEntry;
import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.entity.LedgerVoucher;
import bridge.bridge_bank.domain.ledger.repository.LedgerEntryRepository;
import bridge.bridge_bank.domain.ledger.repository.LedgerQueryRepository;
import bridge.bridge_bank.domain.ledger.repository.LedgerVoucherRepository;
import bridge.bridge_bank.domain.transfer.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerVoucherRepository ledgerVoucherRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public void recordForTransfer(
            BigDecimal transferAmount,
            String transferTransactionGroupId,
            TransferTransactionType transferTransactionType
    ){
        LedgerVoucher ledgerVoucher1 =createAndSaveLedgerVoucher(transferTransactionGroupId, transferTransactionType);
        LedgerVoucher ledgerVoucher2 =createAndSaveLedgerVoucher(transferTransactionGroupId, transferTransactionType);
        List<LedgerEntry> ledgerEntries = new ArrayList<>();

        LedgerEntry ledgerEntryDebitForLedgerVoucher1 = LedgerEntry.create(
                transferAmount,
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.DEBT,
                ledgerVoucher1.getId()
        );
        LedgerEntry ledgerEntryCreditForLedgerVoucher1 = LedgerEntry.create(
                transferAmount,
                LedgerEntryType.CREDIT,
                LedgerBankAssetType.ASSET,
                ledgerVoucher1.getId()
        );

        LedgerEntry ledgerEntryDebitForLedgerVoucher2 = LedgerEntry.create(
                transferAmount,
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.ASSET,
                ledgerVoucher2.getId()
        );
        LedgerEntry ledgerEntryCreditForLedgerVoucher2 = LedgerEntry.create(
                transferAmount,
                LedgerEntryType.CREDIT,
                LedgerBankAssetType.DEBT,
                ledgerVoucher2.getId()
        );

        /*ledgerEntryDebitForLedgerVoucher1.bindWithLedgerVoucher(ledgerVoucher1);
        ledgerEntryCreditForLedgerVoucher1.bindWithLedgerVoucher(ledgerVoucher1);

        ledgerEntryDebitForLedgerVoucher2.bindWithLedgerVoucher(ledgerVoucher2);
        ledgerEntryCreditForLedgerVoucher2.bindWithLedgerVoucher(ledgerVoucher2);*/

        ledgerEntries.add(ledgerEntryDebitForLedgerVoucher1);
        ledgerEntries.add(ledgerEntryCreditForLedgerVoucher1);
        ledgerEntries.add(ledgerEntryDebitForLedgerVoucher2);
        ledgerEntries.add(ledgerEntryCreditForLedgerVoucher2);

        ledgerEntryRepository.saveAll(ledgerEntries);
    }

    private LedgerVoucher createAndSaveLedgerVoucher(
            String transferTransactionGroupId,
            TransferTransactionType transferTransactionType
    ){
        LedgerVoucher ledgerVoucher =LedgerVoucher.create(
                transferTransactionGroupId, transferTransactionType
        );
        return ledgerVoucherRepository.save(ledgerVoucher);
    }

}
