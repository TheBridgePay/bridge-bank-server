package bridge.bridge_bank.domain.accounting_validation;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.repository.LedgerQueryRepository;
import bridge.bridge_bank.domain.transfer_transaction.TransferTransactionResultQueryRepository;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingValidationServiceTest {

    @Mock
    TransferTransactionResultQueryRepository transferTransactionResultQueryRepository;

    @Mock
    LedgerQueryRepository ledgerQueryRepository;

    AccountingValidationService accountingValidationService;

    @BeforeEach
    void setUp() {
        // 동기 실행 Executor를 주입하여 CompletableFuture가 즉시 완료되도록 함
        accountingValidationService = new AccountingValidationService(
                Runnable::run,
                transferTransactionResultQueryRepository,
                ledgerQueryRepository
        );
    }

    @Test
    void validate_allMatch_shouldPassWithoutError() {
        TransferTransactionType type = TransferTransactionType.SIMPLE_TRANSFER;
        BigDecimal sum = BigDecimal.valueOf(100000);

        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT))
                .thenReturn(sum);
        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT))
                .thenReturn(sum);
        when(transferTransactionResultQueryRepository.getYesterdaySumByTransferType(type))
                .thenReturn(sum);

        accountingValidationService.validateYesterdayAccountingByTransferType(type);

        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT);
        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT);
        verify(transferTransactionResultQueryRepository).getYesterdaySumByTransferType(type);
    }

    @Test
    void validate_debitCreditMismatch_shouldDetectFailure() {
        TransferTransactionType type = TransferTransactionType.SIMPLE_TRANSFER;

        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT))
                .thenReturn(BigDecimal.valueOf(100000));
        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT))
                .thenReturn(BigDecimal.valueOf(90000));
        when(transferTransactionResultQueryRepository.getYesterdaySumByTransferType(type))
                .thenReturn(BigDecimal.valueOf(100000));

        // 차변/대변 불일치 시 에러 로깅 후 early return (예외 없이 정상 종료)
        accountingValidationService.validateYesterdayAccountingByTransferType(type);

        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT);
        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT);
    }

    @Test
    void validate_creditTransactionMismatch_shouldDetectFailure() {
        TransferTransactionType type = TransferTransactionType.RESERVE_ONCE;

        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT))
                .thenReturn(BigDecimal.valueOf(100000));
        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT))
                .thenReturn(BigDecimal.valueOf(100000));
        when(transferTransactionResultQueryRepository.getYesterdaySumByTransferType(type))
                .thenReturn(BigDecimal.valueOf(80000));

        // 대변/거래합계 불일치 시 에러 로깅 후 early return (예외 없이 정상 종료)
        accountingValidationService.validateYesterdayAccountingByTransferType(type);

        verify(transferTransactionResultQueryRepository).getYesterdaySumByTransferType(type);
    }

    @Test
    void validate_zeroSums_shouldPassValidation() {
        TransferTransactionType type = TransferTransactionType.RESERVE_REPEAT;

        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT))
                .thenReturn(BigDecimal.ZERO);
        when(ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT))
                .thenReturn(BigDecimal.ZERO);
        when(transferTransactionResultQueryRepository.getYesterdaySumByTransferType(type))
                .thenReturn(BigDecimal.ZERO);

        accountingValidationService.validateYesterdayAccountingByTransferType(type);

        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.DEBIT);
        verify(ledgerQueryRepository).getYesterdaySumByTransferTypeAndLedgerType(type, LedgerEntryType.CREDIT);
        verify(transferTransactionResultQueryRepository).getYesterdaySumByTransferType(type);
    }
}
