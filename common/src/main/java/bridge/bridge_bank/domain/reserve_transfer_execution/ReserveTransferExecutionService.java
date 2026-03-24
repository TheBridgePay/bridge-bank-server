package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.entity.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.transfer.dto.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import bridge.bridge_bank.domain.transfer_transaction_result.event.ReserveTransferResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveTransferExecutionService {
    private final TransferService transferService;
    private final ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;
    private final ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;
    private final TransactionTemplate transactionTemplate;

    public ReserveTransferResultEvent executeReserveOnceTransfer(ReserveOnceTransferSchedule reserveOnceTransferSchedule) {
        ReserveTransferResultEvent event;
        try {
            String groupId = transactionTemplate.execute(status -> {
                return transferService.reserveOnceTransferNow(
                        TransferRequest.create(
                                reserveOnceTransferSchedule.getSenderAccountNumber(),
                                "",
                                reserveOnceTransferSchedule.getReceiverAccountNumber(),
                                reserveOnceTransferSchedule.getTransferAmount()
                        )
                );
            });

            event = ReserveTransferResultEvent.success(
                    groupId,
                    TransferTransactionType.RESERVE_ONCE_OUT.name(),
                    reserveOnceTransferSchedule.getTransferAmount(),
                    reserveOnceTransferSchedule.getSenderAccountNumber(),
                    reserveOnceTransferSchedule.getReceiverAccountNumber(),
                    LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            );
        } catch (Exception e) {
            log.error("reserve once transfer failed - schedule id -{}",
                    reserveOnceTransferSchedule.getId(), e);

            event = ReserveTransferResultEvent.fail(
                    TransferTransactionType.RESERVE_ONCE_OUT.name(),
                    reserveOnceTransferSchedule.getTransferAmount(),
                    reserveOnceTransferSchedule.getSenderAccountNumber(),
                    reserveOnceTransferSchedule.getReceiverAccountNumber(),
                    e.getMessage()
            );
        } finally {
            transactionTemplate.execute(status -> {
                reserveOnceTransferScheduleService.deleteReserveOnceTransferScheduleById(
                        reserveOnceTransferSchedule.getId()
                );
                return null;
            });
        }
        return event;
    }

    public ReserveTransferResultEvent executeReserveRepeatTransfer(ReserveRepeatTransferSchedule reserveRepeatTransferSchedule) {
        ReserveTransferResultEvent event;
        try {
            String groupId = transactionTemplate.execute(status -> {
                return transferService.reserveRepeatTransferNow(
                        TransferRequest.create(
                                reserveRepeatTransferSchedule.getSenderAccountNumber(),
                                "",
                                reserveRepeatTransferSchedule.getReceiverAccountNumber(),
                                reserveRepeatTransferSchedule.getTransferAmount()
                        )
                );
            });

            event = ReserveTransferResultEvent.success(
                    groupId,
                    TransferTransactionType.RESERVE_REPEAT_OUT.name(),
                    reserveRepeatTransferSchedule.getTransferAmount(),
                    reserveRepeatTransferSchedule.getSenderAccountNumber(),
                    reserveRepeatTransferSchedule.getReceiverAccountNumber(),
                    LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            );
        } catch (Exception e) {
            log.error("reserve repeat transfer failed - schedule id -{}",
                    reserveRepeatTransferSchedule.getId(), e);

            event = ReserveTransferResultEvent.fail(
                    TransferTransactionType.RESERVE_REPEAT_OUT.name(),
                    reserveRepeatTransferSchedule.getTransferAmount(),
                    reserveRepeatTransferSchedule.getSenderAccountNumber(),
                    reserveRepeatTransferSchedule.getReceiverAccountNumber(),
                    e.getMessage()
            );
        } finally {
            transactionTemplate.execute(status -> {
                reserveRepeatTransferScheduleService.renewReserveRepeatTransferSchedule(
                        reserveRepeatTransferSchedule
                );
                return null;
            });
        }
        return event;
    }
}
