package bridge.bridge_bank.domain.ledger;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntry;
import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.entity.LedgerVoucher;
import bridge.bridge_bank.domain.ledger.repository.LedgerEntryRepository;
import bridge.bridge_bank.domain.ledger.repository.LedgerVoucherRepository;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    LedgerVoucherRepository ledgerVoucherRepository;

    @Mock
    LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    LedgerService ledgerService;

    @Captor
    ArgumentCaptor<List<LedgerEntry>> entriesCaptor;

    @Test
    void recordForTransfer_shouldCreate2Vouchers() {
        when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
                .thenAnswer(invocation -> {
                    LedgerVoucher v = invocation.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        ledgerService.recordForTransfer(
                BigDecimal.valueOf(50000), "TXN-001", TransferTransactionType.SIMPLE_TRANSFER
        );

        verify(ledgerVoucherRepository, times(2)).save(any(LedgerVoucher.class));
    }

    @Test
    void recordForTransfer_shouldCreate4Entries() {
        when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
                .thenAnswer(invocation -> {
                    LedgerVoucher v = invocation.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        ledgerService.recordForTransfer(
                BigDecimal.valueOf(50000), "TXN-001", TransferTransactionType.SIMPLE_TRANSFER
        );

        verify(ledgerEntryRepository, times(1)).saveAll(entriesCaptor.capture());
        List<LedgerEntry> entries = entriesCaptor.getValue();
        assertThat(entries).hasSize(4);
    }

    @Test
    void recordForTransfer_shouldHaveMatchingDebitAndCreditPairs() {
        when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
                .thenAnswer(invocation -> {
                    LedgerVoucher v = invocation.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        ledgerService.recordForTransfer(
                BigDecimal.valueOf(50000), "TXN-001", TransferTransactionType.SIMPLE_TRANSFER
        );

        verify(ledgerEntryRepository).saveAll(entriesCaptor.capture());
        List<LedgerEntry> entries = entriesCaptor.getValue();

        long debitCount = entries.stream()
                .filter(e -> e.getLedgerEntryType() == LedgerEntryType.DEBIT)
                .count();
        long creditCount = entries.stream()
                .filter(e -> e.getLedgerEntryType() == LedgerEntryType.CREDIT)
                .count();

        assertThat(debitCount).isEqualTo(2);
        assertThat(creditCount).isEqualTo(2);
    }

    @Test
    void recordForTransfer_allEntriesShouldHaveSameAmount() {
        BigDecimal transferAmount = BigDecimal.valueOf(75000);
        when(ledgerVoucherRepository.save(any(LedgerVoucher.class)))
                .thenAnswer(invocation -> {
                    LedgerVoucher v = invocation.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        ledgerService.recordForTransfer(
                transferAmount, "TXN-001", TransferTransactionType.SIMPLE_TRANSFER
        );

        verify(ledgerEntryRepository).saveAll(entriesCaptor.capture());
        List<LedgerEntry> entries = entriesCaptor.getValue();

        entries.forEach(entry ->
                assertThat(entry.getAmount()).isEqualByComparingTo(transferAmount)
        );
    }
}
