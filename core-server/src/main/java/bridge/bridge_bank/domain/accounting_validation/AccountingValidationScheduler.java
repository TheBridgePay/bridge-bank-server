package bridge.bridge_bank.domain.accounting_validation;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AccountingValidationScheduler {


    private final AccountingValidationService accountingValidationService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runAccountingValidationJob() {
        List<Boolean> validateResults = new ArrayList<Boolean>(6);
        validateResults.set(
                TransferTransactionType.SIMPLE_TRANSFER_IN.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.SIMPLE_TRANSFER_IN)
        );
        validateResults.set(
                TransferTransactionType.RESERVE_ONCE_IN.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_ONCE_IN)
        );
        validateResults.set(
                TransferTransactionType.RESERVE_REPEAT_IN.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_REPEAT_IN)
        );

        validateResults.set(
                TransferTransactionType.SIMPLE_TRANSFER_OUT.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.SIMPLE_TRANSFER_OUT)
        );
        validateResults.set(
                TransferTransactionType.RESERVE_ONCE_OUT.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_ONCE_OUT)
        );
        validateResults.set(
                TransferTransactionType.RESERVE_REPEAT_OUT.ordinal(),
                accountingValidationService
                        .validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_REPEAT_OUT)
        );

        //accountingValidationService.check
    }
}
