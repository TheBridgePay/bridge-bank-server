package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveRepeatTransferSchedulerTest {

    @Mock
    private ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;

    @Mock
    private ReserveTransferExecutionService reserveTransferExecutionService;

    @InjectMocks
    private ReserveRepeatTransferScheduler scheduler;

    private static final Executor DIRECT_EXECUTOR = Runnable::run;

    private void injectDirectExecutor() {
        try {
            var field = ReserveRepeatTransferScheduler.class
                    .getDeclaredField("asyncReserveTransferRepeatExecutor");
            field.setAccessible(true);
            field.set(scheduler, DIRECT_EXECUTOR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ReserveRepeatTransferSchedule createSchedule(
            Long id, String sender, String receiver, RepeatType repeatType, int repeatValue) {
        return ReserveRepeatTransferSchedule.builder()
                .id(id)
                .senderAccountNumber(sender)
                .receiverAccountNumber(receiver)
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .repeatType(repeatType)
                .repeatValue(repeatValue)
                .build();
    }

    @Test
    @DisplayName("대기 중인 스케줄이 없으면 실행 서비스를 호출하지 않음")
    void noSchedules_noExecution() {
        // given
        injectDirectExecutor();
        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(Collections.emptyList());

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService, never()).executeReserveRepeatTransfer(any());
    }

    @Test
    @DisplayName("단일 스케줄 정상 실행")
    void singleSchedule_executed() {
        // given
        injectDirectExecutor();
        ReserveRepeatTransferSchedule schedule =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(schedule));

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService, times(1))
                .executeReserveRepeatTransfer(schedule);
    }

    @Test
    @DisplayName("독립적인 스케줄 여러 개 - 모두 실행됨")
    void multipleIndependentSchedules_allExecuted() {
        // given
        injectDirectExecutor();
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "CCC", "DDD", RepeatType.WEEKLY, 1);
        ReserveRepeatTransferSchedule s3 =
                createSchedule(3L, "EEE", "FFF", RepeatType.MONTHLY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s2);
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s3);
    }

    @Test
    @DisplayName("계좌를 공유하는 스케줄들 - 같은 파티션으로 묶여 모두 실행됨")
    void linkedSchedules_samePartition_allExecuted() {
        // given
        injectDirectExecutor();
        // A->B, B->C → 같은 파티션
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "BBB", "CCC", RepeatType.DAILY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2));

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s2);
    }

    @Test
    @DisplayName("전이적 관계 (A->B, B->C, C->D) - 하나의 파티션으로 묶임")
    void transitivelyLinkedSchedules_singlePartition() {
        // given
        injectDirectExecutor();
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "BBB", "CCC", RepeatType.WEEKLY, 2);
        ReserveRepeatTransferSchedule s3 =
                createSchedule(3L, "CCC", "DDD", RepeatType.MONTHLY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService, times(3)).executeReserveRepeatTransfer(any());
    }

    @Test
    @DisplayName("혼합: 연결된 파티션 + 독립 파티션 - 모두 실행됨")
    void mixedPartitions_allExecuted() {
        // given
        injectDirectExecutor();
        // 파티션1: A->B, B->C (연결)
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "BBB", "CCC", RepeatType.DAILY, 1);
        // 파티션2: D->E (독립)
        ReserveRepeatTransferSchedule s3 =
                createSchedule(3L, "DDD", "EEE", RepeatType.HOURLY, 2);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService, times(3)).executeReserveRepeatTransfer(any());
    }

    @Test
    @DisplayName("하나의 스케줄 실행 실패 시 나머지 스케줄은 정상 실행")
    void oneFailure_othersStillExecute() {
        // given
        injectDirectExecutor();
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "CCC", "DDD", RepeatType.WEEKLY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2));

        doThrow(new RuntimeException("실행 실패"))
                .when(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then - s1 실패해도 s2는 실행됨
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s2);
    }

    @Test
    @DisplayName("같은 파티션 내 하나 실패해도 나머지 순차 실행 계속됨")
    void samePartitionFailure_continuesWithRest() {
        // given
        injectDirectExecutor();
        // sender A를 공유하여 같은 파티션
        ReserveRepeatTransferSchedule s1 =
                createSchedule(1L, "AAA", "BBB", RepeatType.DAILY, 1);
        ReserveRepeatTransferSchedule s2 =
                createSchedule(2L, "AAA", "CCC", RepeatType.MONTHLY, 1);

        when(reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(any()))
                .thenReturn(List.of(s1, s2));

        doThrow(new RuntimeException("잔액 부족"))
                .when(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);

        // when
        scheduler.executeReserveRepeatTransferExecution();

        // then
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveRepeatTransfer(s2);
    }
}
