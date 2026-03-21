package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.transfer.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveTransferExecutionService {
    private final TransferService transferService;
    private final ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;
    private final ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;
    private final TransactionTemplate transactionTemplate;

    public void executeReserveOnceTransfer(ReserveOnceTransferSchedule reserveOnceTransferSchedule) {
        try {
            transactionTemplate.execute(status -> {
                transferService.reserveOnceTransferNow(
                        TransferRequest.create(
                                reserveOnceTransferSchedule.getSenderAccountNumber(),
                                "",
                                reserveOnceTransferSchedule.getReceiverAccountNumber(),
                                reserveOnceTransferSchedule.getTransferAmount()
                        )
                );
                return null;
            });
        } catch (Exception e) {
            log.error("reserve once transfer failed - schedule id -{}",
                    reserveOnceTransferSchedule.getId(), e);
        } finally {
            transactionTemplate.execute(status -> {
                reserveOnceTransferScheduleService.deleteReserveOnceTransferScheduleById(
                        reserveOnceTransferSchedule.getId()
                );
                return null;
            });
        }
    }

    public void executeReserveRepeatTransfer(ReserveRepeatTransferSchedule reserveRepeatTransferSchedule) {
        try {
            transactionTemplate.execute(status -> {
                transferService.reserveRepeatTransferNow(
                        TransferRequest.create(
                                reserveRepeatTransferSchedule.getSenderAccountNumber(),
                                "",
                                reserveRepeatTransferSchedule.getReceiverAccountNumber(),
                                reserveRepeatTransferSchedule.getTransferAmount()
                        )
                );
                return null;
            });
        } catch (Exception e) {
            log.error("reserve repeat transfer failed - schedule id -{}",
                    reserveRepeatTransferSchedule.getId(), e);
        } finally {
            transactionTemplate.execute(status -> {
                reserveRepeatTransferScheduleService.renewReserveRepeatTransferSchedule(
                        reserveRepeatTransferSchedule
                );
                return null;
            });
        }
    }
}
