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

    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }

    /**
     * 두 계좌를 데드락 방지를 위해 계좌번호 오름차순으로 비관적 잠금(SELECT FOR UPDATE) 후 조회.
     * @return index 0 = accountNumber1에 해당하는 Account, index 1 = accountNumber2에 해당하는 Account
     */
    public Account[] getTwoAccountsForUpdate(String accountNumber1, String accountNumber2) {
        boolean firstIsSmaller = accountNumber1.compareTo(accountNumber2) < 0;
        String first = firstIsSmaller ? accountNumber1 : accountNumber2;
        String second = firstIsSmaller ? accountNumber2 : accountNumber1;

        Account firstAccount = accountRepository.getAccountByAccountNumberForUpdate(first)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + first));
        Account secondAccount = accountRepository.getAccountByAccountNumberForUpdate(second)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + second));

        if (firstIsSmaller) {
            return new Account[]{firstAccount, secondAccount};
        } else {
            return new Account[]{secondAccount, firstAccount};
        }
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
