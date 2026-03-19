package bridge.bridge_bank.domain.account;

import bridge.bridge_bank.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> getAccountByAccountNumber(String accountNumber);

    @Modifying(flushAutomatically = true)
    @Query(value = "update Account a set a.balance=:amount where a.accountNumber=:accountNumber")
    void updateBalance(String accountNumber, BigDecimal amount);
}
