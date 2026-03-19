package bridge.bridge_bank.domain.transfer.reserve.once;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
}
