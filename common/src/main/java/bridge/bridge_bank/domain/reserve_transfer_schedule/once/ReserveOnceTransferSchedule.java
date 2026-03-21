package bridge.bridge_bank.domain.reserve_transfer_schedule.once;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class ReserveOnceTransferSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_once_transfer_schedule_id")
    private Long id;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private BigDecimal transferAmount;

    private LocalDateTime transferDateTime;

    public static ReserveOnceTransferSchedule create(
            String senderAccountNumber,
            String receiverAccountNumber,
            BigDecimal transferAmount,
            LocalDateTime transferDateTime
    ){
        return ReserveOnceTransferSchedule.builder()
                .senderAccountNumber(senderAccountNumber)
                .receiverAccountNumber(receiverAccountNumber)
                .transferAmount(transferAmount)
                .transferDateTime(transferDateTime)
                .build();
    }
}
