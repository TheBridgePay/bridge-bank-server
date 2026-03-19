package bridge.bridge_bank.domain.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private String memberName;

    private String password;

    private BigDecimal balance;

    public static Account create(
            String memberName,
            String password,
            BigDecimal balance
    ) {
        return Account.builder()
                .accountNumber(""+System.currentTimeMillis())
                .memberName(memberName)
                .password(password)
                .balance(
                        BigDecimal.valueOf(
                                Math.floor((Math.random()*100_000_000))//임시값
                        )
                )
                .build();
    }
}
