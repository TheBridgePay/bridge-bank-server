package bridge.bridge_bank.domain.reserve_transfer_schedule.once;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveOnceTransferScheduleService {
    private final AccountService accountService;
    private final ReserveOnceTransferScheduleRepository reserveOnceTransferScheduleRepository;
    private final ReserveOnceTransferScheduleQueryRepository reserveOnceTransferScheduleQueryRepository;

    @Transactional
    public void createReserveOnceTransferSchedule(
            ReserveOnceTransferScheduleCreateRequest reserveOnceTransferScheduleCreateRequest
    ) {
        if(reserveOnceTransferScheduleCreateRequest
                .getSenderAccount().equals(reserveOnceTransferScheduleCreateRequest.getReceiverAccount())) {
            throw new IllegalArgumentException("Sender and Receiver account cannot be the same");
        }

        Account[] accounts = accountService.getTwoAccountsForUpdate(
                reserveOnceTransferScheduleCreateRequest.getSenderAccount(),
                reserveOnceTransferScheduleCreateRequest.getReceiverAccount());
        Account senderAccount = accounts[0];

        if(!senderAccount.getPassword().equals(reserveOnceTransferScheduleCreateRequest.getSenderPassword())){
            throw new IllegalArgumentException("Sender account password does not match sender account password");
        }

        ReserveOnceTransferSchedule reserveOnceTransferSchedule = ReserveOnceTransferSchedule.create(
                reserveOnceTransferScheduleCreateRequest.getSenderAccount(),
                reserveOnceTransferScheduleCreateRequest.getReceiverAccount(),
                reserveOnceTransferScheduleCreateRequest.getTransferAmount(),
                reserveOnceTransferScheduleCreateRequest.getTransferDateTime()
        );

        reserveOnceTransferScheduleRepository.save(reserveOnceTransferSchedule);
    }

    @Transactional(readOnly = true)
    public List<ReserveOnceTransferSchedule> getPendingReserveOnceTransferSchedules(LocalDateTime now){
        return reserveOnceTransferScheduleQueryRepository.getPendingReserveOnceTransferSchedules(now);
    }

    @Transactional(readOnly = true)
    public List<ReserveOnceTransferSchedule> getReserveOnceTransferSchedules(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ) {
        return reserveOnceTransferScheduleQueryRepository.getReserveOnceTransferSchedules(
                senderAccountNumber, reserveOnceTransferScheduleTargetOption
        );
    }

    @Transactional
    public void deleteReserveOnceTransferSchedule(
            ReserveOnceTransferSchedule reserveOnceTransferSchedule
    ) {
        reserveOnceTransferScheduleRepository.delete(reserveOnceTransferSchedule);
    }

    @Transactional
    public void deleteReserveOnceTransferSchedules(
            String senderAccountNumber,
            ReserveOnceTransferScheduleTargetOption reserveOnceTransferScheduleTargetOption
    ) {
        reserveOnceTransferScheduleQueryRepository.deleteReserveOnceTransferSchedule(
                senderAccountNumber, reserveOnceTransferScheduleTargetOption
        );
    }
}
