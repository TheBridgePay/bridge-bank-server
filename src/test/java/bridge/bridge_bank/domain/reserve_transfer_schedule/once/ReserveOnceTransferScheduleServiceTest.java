package bridge.bridge_bank.domain.reserve_transfer_schedule.once;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleRepository;
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
class ReserveOnceTransferScheduleServiceTest {

    @Mock
    ReserveOnceTransferScheduleRepository reserveOnceTransferScheduleRepository;

    @Mock
    ReserveOnceTransferScheduleQueryRepository reserveOnceTransferScheduleQueryRepository;

    @InjectMocks
    ReserveOnceTransferScheduleService service;

    @Test
    void insertReserveOnceTransferSchedules_shouldCallSaveAll() {
        List<ReserveOnceTransferSchedule> schedules = List.of(
                ReserveOnceTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .transferAmount(BigDecimal.valueOf(50000))
                        .transferDateTime(LocalDateTime.of(2026, 4, 1, 9, 0))
                        .build(),
                ReserveOnceTransferSchedule.builder()
                        .senderAccountNumber("333")
                        .receiverAccountNumber("444")
                        .transferAmount(BigDecimal.valueOf(100000))
                        .transferDateTime(LocalDateTime.of(2026, 5, 1, 10, 0))
                        .build()
        );

        service.insertReserveOnceTransferSchedules(schedules);

        verify(reserveOnceTransferScheduleRepository, times(1)).saveAll(schedules);
    }

    @Test
    void getReserveOnceTransferSchedules_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveOnceTransferScheduleTargetOption option = new ReserveOnceTransferScheduleTargetOption();
        option.setReceiverAccountNumber("222");

        List<ReserveOnceTransferSchedule> expected = List.of(
                ReserveOnceTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .transferAmount(BigDecimal.valueOf(50000))
                        .build()
        );
        when(reserveOnceTransferScheduleQueryRepository
                .getReserveOnceTransferSchedules(senderAccountNumber, option))
                .thenReturn(expected);

        List<ReserveOnceTransferSchedule> result = service.getReserveOnceTransferSchedules(senderAccountNumber, option);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReceiverAccountNumber()).isEqualTo("222");
        verify(reserveOnceTransferScheduleQueryRepository, times(1))
                .getReserveOnceTransferSchedules(senderAccountNumber, option);
    }

    @Test
    void getReserveOnceTransferSchedules_withNullReceiver_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveOnceTransferScheduleTargetOption option = new ReserveOnceTransferScheduleTargetOption();

        List<ReserveOnceTransferSchedule> expected = List.of(
                ReserveOnceTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("222")
                        .build(),
                ReserveOnceTransferSchedule.builder()
                        .senderAccountNumber("111")
                        .receiverAccountNumber("333")
                        .build()
        );
        when(reserveOnceTransferScheduleQueryRepository
                .getReserveOnceTransferSchedules(senderAccountNumber, option))
                .thenReturn(expected);

        List<ReserveOnceTransferSchedule> result = service.getReserveOnceTransferSchedules(senderAccountNumber, option);

        assertThat(result).hasSize(2);
        verify(reserveOnceTransferScheduleQueryRepository, times(1))
                .getReserveOnceTransferSchedules(senderAccountNumber, option);
    }

    @Test
    void deleteReserveOnceTransferSchedules_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveOnceTransferScheduleTargetOption option = new ReserveOnceTransferScheduleTargetOption();
        option.setReceiverAccountNumber("222");

        service.deleteReserveOnceTransferSchedules(senderAccountNumber, option);

        verify(reserveOnceTransferScheduleQueryRepository, times(1))
                .deleteReserveOnceTransferSchedule(senderAccountNumber, option);
    }

    @Test
    void deleteReserveOnceTransferSchedules_withNullReceiver_shouldDelegateToQueryRepository() {
        String senderAccountNumber = "111";
        ReserveOnceTransferScheduleTargetOption option = new ReserveOnceTransferScheduleTargetOption();

        service.deleteReserveOnceTransferSchedules(senderAccountNumber, option);

        verify(reserveOnceTransferScheduleQueryRepository, times(1))
                .deleteReserveOnceTransferSchedule(senderAccountNumber, option);
    }
}
