package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveRepeatTransferScheduleService {
    private final ReserveRepeatTransferScheduleRepository reserveRepeatTransferScheduleRepository;
    private final ReserveRepeatTransferScheduleQueryRepository reserveRepeatTransferScheduleQueryRepository;
    private final AccountService accountService;

    @Transactional
    public void createReserveRepeatTransferSchedule(
            ReserveRepeatTransferScheduleCreateRequest reserveRepeatTransferScheduleCreateRequest
    ) {
        if(reserveRepeatTransferScheduleCreateRequest.getSenderAccount()
                .equals(reserveRepeatTransferScheduleCreateRequest.getReceiverAccount())) {
            throw new IllegalArgumentException("Sender and Receiver account cannot be the same");
        }

        Account[] accounts = accountService.getTwoAccounts(
                reserveRepeatTransferScheduleCreateRequest.getSenderAccount(),
                reserveRepeatTransferScheduleCreateRequest.getReceiverAccount());
        Account senderAccount = accounts[0];

        if(!senderAccount.getPassword().equals(reserveRepeatTransferScheduleCreateRequest.getSenderPassword())){
            throw new IllegalArgumentException("Sender account password does not match sender account password");
        }

        ReserveRepeatTransferSchedule reserveRepeatTransferSchedule = ReserveRepeatTransferSchedule.create(
                reserveRepeatTransferScheduleCreateRequest.getSenderAccount(),
                reserveRepeatTransferScheduleCreateRequest.getReceiverAccount(),
                reserveRepeatTransferScheduleCreateRequest.getTransferAmount(),
                reserveRepeatTransferScheduleCreateRequest.getTransferDateTime(),
                reserveRepeatTransferScheduleCreateRequest.getRepeatType(),
                reserveRepeatTransferScheduleCreateRequest.getRepeatValue()
        );

        reserveRepeatTransferScheduleRepository.save(reserveRepeatTransferSchedule);
    }

    @Transactional(readOnly = true)
    public List<ReserveRepeatTransferSchedule> getPendingReserveRepeatTransferSchedules(LocalDateTime now){
        return reserveRepeatTransferScheduleQueryRepository.getPendingReserveRepeatTransferSchedules(now);
    }

    @Transactional(readOnly = true)
    public List<ReserveRepeatTransferSchedule> getReserveRepeatTransferSchedules(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ) {
        return reserveRepeatTransferScheduleQueryRepository.getReserveRepeatTransferSchedules(
                senderAccountNumber, reserveRepeatTransferScheduleTargetOption
        );
    }

    @Transactional
    public void renewReserveRepeatTransferSchedule(
            ReserveRepeatTransferSchedule reserveRepeatTransferSchedule
    ){
        RepeatType repeatType = reserveRepeatTransferSchedule.getRepeatType();
        Integer repeatValue = reserveRepeatTransferSchedule.getRepeatValue();
        LocalDateTime oldDateTime = reserveRepeatTransferSchedule.getTransferDateTime();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDateTime = oldDateTime;
        while (newDateTime.isBefore(now)) {
            newDateTime = switch (repeatType) {
                case MONTHLY -> newDateTime.plusMonths(repeatValue);
                case WEEKLY  -> newDateTime.plusWeeks(repeatValue);
                case DAILY   -> newDateTime.plusDays(repeatValue);
                case HOURLY  -> newDateTime.plusHours(repeatValue);
            };
        }

        reserveRepeatTransferScheduleRepository.updateTransferDateTimeById(
                reserveRepeatTransferSchedule.getId(),
                newDateTime
        );
    }

    @Transactional
    public void deleteReserveRepeatTransferScheduleById(
            Long reserveRepeatTransferScheduleId
    ) {
        reserveRepeatTransferScheduleRepository.deleteById(reserveRepeatTransferScheduleId);
    }

    @Transactional
    public void deleteReserveRepeatTransferSchedules(
            String senderAccountNumber,
            ReserveRepeatTransferScheduleTargetOption reserveRepeatTransferScheduleTargetOption
    ){
        reserveRepeatTransferScheduleQueryRepository.deleteReserveRepeatTransferSchedule(
                senderAccountNumber, reserveRepeatTransferScheduleTargetOption
        );
    }
}
