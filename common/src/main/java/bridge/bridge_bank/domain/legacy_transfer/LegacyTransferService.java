package bridge.bridge_bank.domain.legacy_transfer;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.transfer.dto.TransferRequest;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResultStatus;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import bridge.bridge_bank.global.error.InsufficientBalanceException;
import bridge.bridge_bank.global.error.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegacyTransferService {
    private final AccountService accountService;
    private final LegacyTransferTransactionResultService legacyTransferTransactionResultService;
    private final LegacyLedgerService legacyLedgerService;

    @Transactional
    public void simpleTransferNow(TransferRequest transferRequest) {
        if (transferRequest.getSenderAccount().equals(transferRequest.getReceiverAccount())) {
            throw new IllegalArgumentException("Sender and Receiver account cannot be the same");
        }

        Account[] accounts = accountService.getTwoAccountsForUpdate(
                transferRequest.getSenderAccount(), transferRequest.getReceiverAccount());
        Account senderAccount = accounts[0];
        Account receiverAccount = accounts[1];

        if (!senderAccount.getPassword().equals(transferRequest.getSenderPassword())) {
            throw new PasswordMismatchException("sender password not match");
        }

        if (senderAccount.getBalance().compareTo(transferRequest.getTransferAmount()) < 0) {
            throw new InsufficientBalanceException("sender balance not enough");
        }

        BigDecimal senderBeforeBalance = senderAccount.getBalance();
        BigDecimal receiverBeforeBalance = receiverAccount.getBalance();
        BigDecimal newSenderAccountBalance = senderBeforeBalance.subtract(transferRequest.getTransferAmount());
        BigDecimal newReceiverAccountBalance = receiverBeforeBalance.add(transferRequest.getTransferAmount());

        senderAccount.setBalance(newSenderAccountBalance);
        receiverAccount.setBalance(newReceiverAccountBalance);

        String transferTransactionGroupId = UUID.randomUUID().toString();
        TransferTransactionResult senderTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.SIMPLE_TRANSFER_OUT,
                transferRequest.getTransferAmount(),
                senderBeforeBalance,
                newSenderAccountBalance,
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber()
        );
        TransferTransactionResult receiverTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.SIMPLE_TRANSFER_IN,
                transferRequest.getTransferAmount(),
                receiverBeforeBalance,
                newReceiverAccountBalance,
                receiverAccount.getAccountNumber(),
                senderAccount.getAccountNumber()
        );

        legacyTransferTransactionResultService.saveTransferTransactionResultBoth(
                senderTransferTransactionResult,
                receiverTransferTransactionResult
        );

        legacyLedgerService.recordForTransfer(
                transferRequest.getTransferAmount(),
                transferTransactionGroupId,
                TransferTransactionType.SIMPLE_TRANSFER_OUT,
                TransferTransactionType.SIMPLE_TRANSFER_IN
        );
    }

    @Transactional
    public String reserveOnceTransferNow(TransferRequest transferRequest) {
        if (transferRequest.getSenderAccount().equals(transferRequest.getReceiverAccount())) {
            throw new IllegalArgumentException("Sender and Receiver account cannot be the same");
        }

        Account[] accounts = accountService.getTwoAccountsForUpdate(
                transferRequest.getSenderAccount(), transferRequest.getReceiverAccount());
        Account senderAccount = accounts[0];
        Account receiverAccount = accounts[1];

        if (senderAccount.getBalance().compareTo(transferRequest.getTransferAmount()) < 0) {
            throw new InsufficientBalanceException("sender balance not enough");
        }

        BigDecimal senderBeforeBalance = senderAccount.getBalance();
        BigDecimal receiverBeforeBalance = receiverAccount.getBalance();
        BigDecimal newSenderAccountBalance = senderBeforeBalance.subtract(transferRequest.getTransferAmount());
        BigDecimal newReceiverAccountBalance = receiverBeforeBalance.add(transferRequest.getTransferAmount());

        senderAccount.setBalance(newSenderAccountBalance);
        receiverAccount.setBalance(newReceiverAccountBalance);

        String transferTransactionGroupId = UUID.randomUUID().toString();
        TransferTransactionResult senderTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.RESERVE_ONCE_OUT,
                transferRequest.getTransferAmount(),
                senderBeforeBalance,
                newSenderAccountBalance,
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber()
        );
        TransferTransactionResult receiverTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.RESERVE_ONCE_IN,
                transferRequest.getTransferAmount(),
                receiverBeforeBalance,
                newReceiverAccountBalance,
                receiverAccount.getAccountNumber(),
                senderAccount.getAccountNumber()
        );

        legacyTransferTransactionResultService.saveTransferTransactionResultBoth(
                senderTransferTransactionResult,
                receiverTransferTransactionResult
        );

        legacyLedgerService.recordForTransfer(
                transferRequest.getTransferAmount(),
                transferTransactionGroupId,
                TransferTransactionType.RESERVE_ONCE_OUT,
                TransferTransactionType.RESERVE_ONCE_IN
        );

        return transferTransactionGroupId;
    }

    @Transactional
    public String reserveRepeatTransferNow(TransferRequest transferRequest) {
        if (transferRequest.getSenderAccount().equals(transferRequest.getReceiverAccount())) {
            throw new IllegalArgumentException("Sender and Receiver account cannot be the same");
        }

        Account[] accounts = accountService.getTwoAccountsForUpdate(
                transferRequest.getSenderAccount(), transferRequest.getReceiverAccount());
        Account senderAccount = accounts[0];
        Account receiverAccount = accounts[1];

        if (senderAccount.getBalance().compareTo(transferRequest.getTransferAmount()) < 0) {
            throw new InsufficientBalanceException("sender balance not enough");
        }

        BigDecimal senderBeforeBalance = senderAccount.getBalance();
        BigDecimal receiverBeforeBalance = receiverAccount.getBalance();
        BigDecimal newSenderAccountBalance = senderBeforeBalance.subtract(transferRequest.getTransferAmount());
        BigDecimal newReceiverAccountBalance = receiverBeforeBalance.add(transferRequest.getTransferAmount());

        senderAccount.setBalance(newSenderAccountBalance);
        receiverAccount.setBalance(newReceiverAccountBalance);

        String transferTransactionGroupId = UUID.randomUUID().toString();
        TransferTransactionResult senderTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.RESERVE_REPEAT_OUT,
                transferRequest.getTransferAmount(),
                senderBeforeBalance,
                newSenderAccountBalance,
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber()
        );
        TransferTransactionResult receiverTransferTransactionResult = TransferTransactionResult.create(
                transferTransactionGroupId,
                TransferTransactionResultStatus.SUCCESS,
                TransferTransactionType.RESERVE_REPEAT_IN,
                transferRequest.getTransferAmount(),
                receiverBeforeBalance,
                newReceiverAccountBalance,
                receiverAccount.getAccountNumber(),
                senderAccount.getAccountNumber()
        );

        legacyTransferTransactionResultService.saveTransferTransactionResultBoth(
                senderTransferTransactionResult,
                receiverTransferTransactionResult
        );

        legacyLedgerService.recordForTransfer(
                transferRequest.getTransferAmount(),
                transferTransactionGroupId,
                TransferTransactionType.RESERVE_REPEAT_OUT,
                TransferTransactionType.RESERVE_REPEAT_IN
        );

        return transferTransactionGroupId;
    }
}
