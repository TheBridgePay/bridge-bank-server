package bridge.bridge_bank.domain.accounting_validation;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountingValidationScheduler {


    private final AccountingValidationService accountingValidationService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runAccountingValidationJob() {
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.SIMPLE_TRANSFER_IN);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_ONCE_IN);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_REPEAT_IN);

        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.SIMPLE_TRANSFER_OUT);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_ONCE_OUT);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_REPEAT_OUT);
    }
}
