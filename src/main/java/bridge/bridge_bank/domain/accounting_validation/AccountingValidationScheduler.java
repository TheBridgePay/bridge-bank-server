package bridge.bridge_bank.domain.accounting_validation;

import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountingValidationScheduler {


    private final AccountingValidationService accountingValidationService;

    @Scheduled(cron = "0 0 4  * * *")
    public void runAccountingValidationJob() {
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.SIMPLE_TRANSFER);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_ONCE);
        accountingValidationService.validateYesterdayAccountingByTransferType(TransferTransactionType.RESERVE_REPEAT);
    }
}
