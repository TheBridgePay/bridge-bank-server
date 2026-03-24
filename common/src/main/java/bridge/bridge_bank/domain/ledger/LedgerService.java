package bridge.bridge_bank.domain.ledger;

import bridge.bridge_bank.domain.ledger.entity.LedgerBankAssetType;
import bridge.bridge_bank.domain.ledger.entity.LedgerEntry;
import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.entity.LedgerVoucher;
import bridge.bridge_bank.domain.ledger.repository.LedgerBulkInsertMapper;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerBulkInsertMapper ledgerBulkInsertMapper;

    @Transactional
    public void recordForTransfer(
            BigDecimal transferAmount,
            String transferTransactionGroupId,
            TransferTransactionType senderTransferTransactionType,
            TransferTransactionType receiverTransferTransactionType
    ){
        List<LedgerVoucher> pairLedgerVoucher =
                createAndSavePairLedgerVoucher(transferTransactionGroupId, senderTransferTransactionType, receiverTransferTransactionType);
        LedgerVoucher ledgerVoucher1 =pairLedgerVoucher.get(0);
        LedgerVoucher ledgerVoucher2 =pairLedgerVoucher.get(1);

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

        ledgerBulkInsertMapper.bulkInsertEntries(ledgerEntries);
    }

    private List<LedgerVoucher> createAndSavePairLedgerVoucher(
            String transferTransactionGroupId,
            TransferTransactionType transferTransactionType1,
            TransferTransactionType transferTransactionType2
    ){
        List<LedgerVoucher> ledgerVouchers = new ArrayList<>();
        LedgerVoucher ledgerVoucher1 =LedgerVoucher.create(
                transferTransactionGroupId, transferTransactionType1
        );
        LedgerVoucher ledgerVoucher2 =LedgerVoucher.create(
                transferTransactionGroupId, transferTransactionType2
        );
        ledgerVouchers.add(ledgerVoucher1);
        ledgerVouchers.add(ledgerVoucher2);

        ledgerBulkInsertMapper.bulkInsertVouchers(ledgerVouchers);
        return ledgerVouchers;
    }

}
