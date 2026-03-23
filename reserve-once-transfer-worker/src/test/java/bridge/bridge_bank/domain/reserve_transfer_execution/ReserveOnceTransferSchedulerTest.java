package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
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
class ReserveOnceTransferSchedulerTest {

    @Mock
    private ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;

    @Mock
    private ReserveTransferExecutionService reserveTransferExecutionService;

    @InjectMocks
    private ReserveOnceTransferScheduler scheduler;

    /**
     * 동기 Executor - 테스트에서 CompletableFuture.runAsync가 같은 스레드에서 실행되도록 한다.
     */
    private static final Executor DIRECT_EXECUTOR = Runnable::run;

    /**
     * Executor를 주입하기 위해 리플렉션으로 필드 세팅.
     * Mockito @InjectMocks는 @Qualifier가 붙은 필드에 적절한 mock을 주입하지 못하므로 수동 설정.
     */
    private void injectDirectExecutor() {
        try {
            var field = ReserveOnceTransferScheduler.class
                    .getDeclaredField("asyncReserveTransferOnceExecutor");
            field.setAccessible(true);
            field.set(scheduler, DIRECT_EXECUTOR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ReserveOnceTransferSchedule createSchedule(Long id, String sender, String receiver) {
        return ReserveOnceTransferSchedule.builder()
                .id(id)
                .senderAccountNumber(sender)
                .receiverAccountNumber(receiver)
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .build();
    }

    @Test
    @DisplayName("대기 중인 스케줄이 없으면 실행 서비스를 호출하지 않음")
    void noSchedules_noExecution() {
        // given
        injectDirectExecutor();
        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(Collections.emptyList());

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then
        verify(reserveTransferExecutionService, never()).executeReserveOnceTransfer(any());
    }

    @Test
    @DisplayName("단일 스케줄 정상 실행")
    void singleSchedule_executed() {
        // given
        injectDirectExecutor();
        ReserveOnceTransferSchedule schedule = createSchedule(1L, "AAA", "BBB");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(schedule));

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then
        verify(reserveTransferExecutionService, times(1)).executeReserveOnceTransfer(schedule);
    }

    @Test
    @DisplayName("독립적인 스케줄 여러 개 - 모두 실행됨")
    void multipleIndependentSchedules_allExecuted() {
        // given
        injectDirectExecutor();
        // 계좌가 겹치지 않는 3개의 독립 스케줄 → 3개 파티션
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "CCC", "DDD");
        ReserveOnceTransferSchedule s3 = createSchedule(3L, "EEE", "FFF");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then - 3개 모두 실행
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s2);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s3);
    }

    @Test
    @DisplayName("계좌를 공유하는 스케줄들 - 같은 파티션으로 묶여 모두 실행됨")
    void linkedSchedules_samePartition_allExecuted() {
        // given
        injectDirectExecutor();
        // A->B, B->C → Union-Find에 의해 같은 파티션으로 묶임
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "BBB", "CCC");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2));

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then - 같은 파티션이더라도 모두 실행됨
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s2);
    }

    @Test
    @DisplayName("전이적 관계 (A->B, B->C, C->D) - 하나의 파티션으로 묶여 모두 실행됨")
    void transitivelyLinkedSchedules_singlePartition_allExecuted() {
        // given
        injectDirectExecutor();
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "BBB", "CCC");
        ReserveOnceTransferSchedule s3 = createSchedule(3L, "CCC", "DDD");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s2);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s3);
        verify(reserveTransferExecutionService, times(3)).executeReserveOnceTransfer(any());
    }

    @Test
    @DisplayName("혼합: 연결된 파티션 + 독립 파티션 - 모두 실행됨")
    void mixedPartitions_allExecuted() {
        // given
        injectDirectExecutor();
        // 파티션1: A->B, B->C (연결)
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "BBB", "CCC");
        // 파티션2: D->E (독립)
        ReserveOnceTransferSchedule s3 = createSchedule(3L, "DDD", "EEE");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then - 모든 스케줄 실행
        verify(reserveTransferExecutionService, times(3)).executeReserveOnceTransfer(any());
    }

    @Test
    @DisplayName("하나의 스케줄 실행 실패 시 나머지 스케줄은 정상 실행")
    void oneFailure_othersStillExecute() {
        // given
        injectDirectExecutor();
        // 같은 파티션에 2개 (순차 실행), 독립 파티션 1개
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "CCC", "DDD");
        ReserveOnceTransferSchedule s3 = createSchedule(3L, "EEE", "FFF");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2, s3));

        // s1 실행 시 예외 발생
        doThrow(new RuntimeException("실행 실패"))
                .when(reserveTransferExecutionService).executeReserveOnceTransfer(s1);

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then - s1 실패해도 s2, s3는 실행됨
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s2);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s3);
    }

    @Test
    @DisplayName("같은 파티션 내 하나 실패해도 나머지 순차 실행 계속됨")
    void samePartitionFailure_continuesWithRest() {
        // given
        injectDirectExecutor();
        // 같은 파티션: A->B, A->C (sender A 공유)
        ReserveOnceTransferSchedule s1 = createSchedule(1L, "AAA", "BBB");
        ReserveOnceTransferSchedule s2 = createSchedule(2L, "AAA", "CCC");

        when(reserveOnceTransferScheduleService.getPendingReserveOnceTransferSchedules(any()))
                .thenReturn(List.of(s1, s2));

        // s1 실행 시 예외
        doThrow(new RuntimeException("잔액 부족"))
                .when(reserveTransferExecutionService).executeReserveOnceTransfer(s1);

        // when
        scheduler.executeReserveOnceTransferExecution();

        // then - s1 실패해도 s2는 실행됨
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s1);
        verify(reserveTransferExecutionService).executeReserveOnceTransfer(s2);
    }
}
