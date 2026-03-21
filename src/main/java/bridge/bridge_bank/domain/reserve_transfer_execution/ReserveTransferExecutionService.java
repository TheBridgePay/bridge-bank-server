package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
import bridge.bridge_bank.domain.transfer.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReserveTransferExecutionService {
    private final TransferService transferService;
    private final ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;

    @Transactional
    public void executeReserveOnceTransfer(ReserveOnceTransferSchedule reserveOnceTransferSchedule) {
        transferService.reserveOnceTransferNow(
                TransferRequest.create(
                        reserveOnceTransferSchedule.getSenderAccountNumber(),
                        "",
                        reserveOnceTransferSchedule.getReceiverAccountNumber(),
                        reserveOnceTransferSchedule.getTransferAmount()
                )
        );
        reserveOnceTransferScheduleService.deleteReserveOnceTransferSchedule(
                reserveOnceTransferSchedule
        );
    }
}
