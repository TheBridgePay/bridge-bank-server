package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_rrst_transfer_date_time",
                columnList = "transferDateTime"),
        @Index(name = "idx_rrst_sender_account",
                columnList = "senderAccountNumber")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class ReserveRepeatTransferSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_repeat_transfer_schedule_id")
    private Long id;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private BigDecimal transferAmount;

    private LocalDateTime transferDateTime;

    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    private Integer repeatValue;

    public static ReserveRepeatTransferSchedule create(
            String senderAccountNumber,
            String receiverAccountNumber,
            BigDecimal transferAmount,
            LocalDateTime transferDateTime,
            RepeatType repeatType,
            Integer repeatValue
    ){
        return ReserveRepeatTransferSchedule.builder()
                .senderAccountNumber(senderAccountNumber)
                .receiverAccountNumber(receiverAccountNumber)
                .transferAmount(transferAmount)
                .transferDateTime(transferDateTime)
                .repeatType(repeatType)
                .repeatValue(repeatValue)
                .build();
    }
}
