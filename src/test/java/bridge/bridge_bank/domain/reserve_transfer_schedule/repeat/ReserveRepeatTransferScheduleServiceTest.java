package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveRepeatTransferScheduleServiceTest {

    @Mock
    ReserveRepeatTransferScheduleRepository reserveRepeatTransferScheduleRepository;

    @Mock
    ReserveRepeatTransferScheduleQueryRepository reserveRepeatTransferScheduleQueryRepository;

    @InjectMocks
    ReserveRepeatTransferScheduleService service;

    @Test
    void insertReserveRepeatTransferSchedules_shouldCallSaveAll() {
        List<ReserveRepeatTransferSchedule> schedules = List.of(
                ReserveRepeatTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .transferAmount(BigDecimal.valueOf(10000))
                        .transferDateTime(LocalDateTime.of(2026, 4, 1, 9, 0))
                        .repeatType(RepeatType.MONTHLY)
                        .repeatValue(15)
                        .build(),
                ReserveRepeatTransferSchedule.builder()
                        .senderAccountNumber("333")
                        .receiverAccountNumber("444")
                        .transferAmount(BigDecimal.valueOf(5000))
                        .transferDateTime(LocalDateTime.of(2026, 4, 1, 9, 0))
                        .repeatType(RepeatType.WEEKLY)
                        .repeatValue(1)
                        .build()
        );

        service.insertReserveRepeatTransferSchedules(schedules);

        verify(reserveRepeatTransferScheduleRepository, times(1)).saveAll(schedules);
    }

    @Test
    void getReserveRepeatTransferSchedules_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveRepeatTransferScheduleTargetOption option = new ReserveRepeatTransferScheduleTargetOption();
        option.setReceiverAccountNumber("222");

        List<ReserveRepeatTransferSchedule> expected = List.of(
                ReserveRepeatTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .transferAmount(BigDecimal.valueOf(10000))
                        .repeatType(RepeatType.DAILY)
                        .build()
        );
        when(reserveRepeatTransferScheduleQueryRepository
                .getReserveRepeatTransferSchedules(senderAccountNumber, option))
                .thenReturn(expected);

        List<ReserveRepeatTransferSchedule> result = service.getReserveRepeatTransferSchedules(senderAccountNumber, option);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReceiverAccountNumber()).isEqualTo("222");
        assertThat(result.get(0).getRepeatType()).isEqualTo(RepeatType.DAILY);
        verify(reserveRepeatTransferScheduleQueryRepository, times(1))
                .getReserveRepeatTransferSchedules(senderAccountNumber, option);
    }

    @Test
    void getReserveRepeatTransferSchedules_withNullReceiver_shouldReturnAll() {
        String senderAccountNumber = "111";
        ReserveRepeatTransferScheduleTargetOption option = new ReserveRepeatTransferScheduleTargetOption();

        List<ReserveRepeatTransferSchedule> expected = List.of(
                ReserveRepeatTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .repeatType(RepeatType.MONTHLY)
                        .build(),
                ReserveRepeatTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("333")
                        .repeatType(RepeatType.WEEKLY)
                        .build()
        );
        when(reserveRepeatTransferScheduleQueryRepository
                .getReserveRepeatTransferSchedules(senderAccountNumber, option))
                .thenReturn(expected);

        List<ReserveRepeatTransferSchedule> result = service.getReserveRepeatTransferSchedules(senderAccountNumber, option);

        assertThat(result).hasSize(2);
        verify(reserveRepeatTransferScheduleQueryRepository, times(1))
                .getReserveRepeatTransferSchedules(senderAccountNumber, option);
    }

    @Test
    void deleteReserveRepeatTransferSchedules_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveRepeatTransferScheduleTargetOption option = new ReserveRepeatTransferScheduleTargetOption();
        option.setReceiverAccountNumber("222");

        service.deleteReserveRepeatTransferSchedules(senderAccountNumber, option);

        verify(reserveRepeatTransferScheduleQueryRepository, times(1))
                .deleteReserveRepeatTransferSchedule(senderAccountNumber, option);
    }

    @Test
    void deleteReserveRepeatTransferSchedules_withNullReceiver_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveRepeatTransferScheduleTargetOption option = new ReserveRepeatTransferScheduleTargetOption();

        service.deleteReserveRepeatTransferSchedules(senderAccountNumber, option);

        verify(reserveRepeatTransferScheduleQueryRepository, times(1))
                .deleteReserveRepeatTransferSchedule(senderAccountNumber, option);
    }
}
