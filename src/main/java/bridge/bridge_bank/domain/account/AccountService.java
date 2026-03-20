package bridge.bridge_bank.domain.account;

import bridge.bridge_bank.domain.account.entity.Account;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount(Account account) {
        accountRepository.save(account);
    }

    public boolean checkAccount(String accountNumber) {
        return getAccount(accountNumber).isPresent();
    }

    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }

    @Transactional
    public void updateAccountBalanceBoth(
            String senderAccountNumber, BigDecimal senderNewBalance,
            String receiverAccountNumber, BigDecimal receiverNewBalance
    ) {
        accountRepository.updateBalanceBoth(
                senderAccountNumber, senderNewBalance,
                receiverAccountNumber, receiverNewBalance
        );
    }
}
