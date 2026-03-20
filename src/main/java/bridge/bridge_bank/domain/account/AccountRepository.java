package bridge.bridge_bank.domain.account;

import bridge.bridge_bank.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> getAccountByAccountNumber(String accountNumber);

    @Modifying
    @Query(value = "update Account a " +
            "set a.balance= case " +
            "when a.accountNumber=:senderAccountNumber then :senderNewBalance " +
            "when a.accountNumber=:receiverAccountNumber then :receiverNewBalance " +
            "end where a.accountNumber in (:senderAccountNumber, :receiverAccountNumber)")
    void updateBalanceBoth(
            String senderAccountNumber, BigDecimal senderNewBalance,
            String receiverAccountNumber, BigDecimal receiverNewBalance
    );
}
