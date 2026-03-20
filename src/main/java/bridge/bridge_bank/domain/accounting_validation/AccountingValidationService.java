package bridge.bridge_bank.domain.accounting_validation;

import bridge.bridge_bank.domain.ledger.entity.LedgerEntryType;
import bridge.bridge_bank.domain.ledger.repository.LedgerQueryRepository;
import bridge.bridge_bank.domain.transfer_transaction.TransferTransactionResultQueryRepository;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingValidationService {
    @Qualifier("asyncAccountingValidationExecutor")
    private final Executor asyncAccountingValidationExecutor;

    private final TransferTransactionResultQueryRepository transferTransactionResultQueryRepository;
    private final LedgerQueryRepository ledgerQueryRepository;

    @Transactional(readOnly = true)
    public void validateYesterdayAccountingByTransferType(TransferTransactionType transferTransactionType) {
        CompletableFuture<BigDecimal> ledgerDebitSumFuture = CompletableFuture.supplyAsync(
                () -> getYesterdayLedgerSumByTransferTypeAndLedgerType(transferTransactionType, LedgerEntryType.DEBIT),
                asyncAccountingValidationExecutor
        );
        CompletableFuture<BigDecimal> ledgerCreditSumFuture = CompletableFuture.supplyAsync(
                () -> getYesterdayLedgerSumByTransferTypeAndLedgerType(transferTransactionType, LedgerEntryType.CREDIT),
                asyncAccountingValidationExecutor
        );
        CompletableFuture<BigDecimal> transactionSumFuture = CompletableFuture.supplyAsync(
                () -> getYesterdayTransactionSumByTransferType(transferTransactionType),
                asyncAccountingValidationExecutor
        );
        CompletableFuture.allOf(ledgerDebitSumFuture,ledgerDebitSumFuture,transactionSumFuture).join();

        BigDecimal ledgerDebitSum = ledgerDebitSumFuture.join();
        BigDecimal ledgerCreditSum = ledgerCreditSumFuture.join();
        BigDecimal transactionSum = transactionSumFuture.join();

        if (ledgerCreditSum.compareTo(ledgerDebitSum) != 0) {//차변 대변 합 안 맞음 ;;;;
            // 정합성 실패 → 회계 무결성이 깨진 심각한 상황
            // Kafka를 통해 ERROR 레벨 로그를 전송하여 운영팀에 즉시 알린다

            //지금은 임시로 로깅
            log.error(transferTransactionType.name() + " 타입 정합성 검증 실패, 거래내역 합: "
                    + "\nTRANSACTION TYPE 합: " + transactionSum
                    + "\nDEBIT TYPE 합: " + ledgerDebitSum
                    + "\nCREDIT TYPE 합: " + ledgerCreditSum
            );
            return;
        }

        if (ledgerCreditSum.compareTo(transactionSum) != 0) {//이체합계값 대변 합 안 맞음 ;;;;
            // 정합성 실패 → 회계 무결성이 깨진 심각한 상황
            // Kafka를 통해 ERROR 레벨 로그를 전송하여 운영팀에 즉시 알린다

            //지금은 임시로 로깅
            log.error(transferTransactionType.name() + " 타입 정합성 검증 실패, 거래내역 합: "
                    + "\nTRANSACTION TYPE 합: " + transactionSum
                    + "\nLEDGER TYPE 합: " + ledgerCreditSum
            );
            return;
        }

        log.info(transferTransactionType.name() + " 타입 정합성 검증 성공, 거래내역 합: " + transactionSum + "\nDEBIT TYPE 합: " + ledgerDebitSum + "\nCREDIT TYPE 합: " + ledgerCreditSum);
    }

    //@Transactional(readOnly = true)
    private BigDecimal getYesterdayTransactionSumByTransferType(TransferTransactionType transferTransactionType) {
        return transferTransactionResultQueryRepository.getYesterdaySumByTransferType(transferTransactionType);
    }

    //@Transactional(readOnly = true)
    private BigDecimal getYesterdayLedgerSumByTransferTypeAndLedgerType(TransferTransactionType transferTransactionType, LedgerEntryType ledgerEntryType) {
        return ledgerQueryRepository.getYesterdaySumByTransferTypeAndLedgerType(transferTransactionType, ledgerEntryType);
    }


}
