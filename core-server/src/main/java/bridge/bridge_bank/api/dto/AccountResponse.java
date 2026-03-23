package bridge.bridge_bank.api.dto;

import bridge.bridge_bank.domain.account.entity.Account;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountResponse {
    private String accountNumber;
    private String memberName;
    private BigDecimal balance;

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getMemberName(),
                account.getBalance()
        );
    }
}
