package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveRepeatTransferScheduler {
    private final ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;
    private final ReserveTransferExecutionService reserveTransferExecutionService;

    @Qualifier("asyncReserveTransferRepeatExecutor")
    private final Executor asyncReserveTransferRepeatExecutor;

    @Scheduled(cron = "*/30 * * * * *")
    public void executeReserveRepeatTransferExecution() {
        LocalDateTime now = LocalDateTime.now();
        List<ReserveRepeatTransferSchedule> schedules
                = reserveRepeatTransferScheduleService.getPendingReserveRepeatTransferSchedules(now);

        // 1. Union-Find로 같은 계좌를 공유하는 스케줄을 같은 집합으로 묶기
        Map<String, String> parent = new HashMap<>();
        for (ReserveRepeatTransferSchedule schedule : schedules) {
            union(parent, schedule.getSenderAccountNumber(), schedule.getReceiverAccountNumber());
        }

        // 2. 집합(root) 기준으로 파티셔닝
        Map<String, List<ReserveRepeatTransferSchedule>> partitions = new HashMap<>();
        for (ReserveRepeatTransferSchedule schedule : schedules) {
            String root = find(parent, schedule.getSenderAccountNumber());
            partitions.computeIfAbsent(root, k -> new ArrayList<>()).add(schedule);
        }

        // 3. 파티션별 병렬 실행, 파티션 내부 순차 처리
        List<CompletableFuture<Void>> futures = partitions.values().stream()
                .map(partition -> CompletableFuture.runAsync(() -> {
                    for (ReserveRepeatTransferSchedule schedule : partition) {
                        try {
                            reserveTransferExecutionService.executeReserveRepeatTransfer(schedule);
                        } catch (Exception e) {
                            log.error("예약 이체 실행 실패, 다음 분에 재시도 - scheduleId: {}",
                                    schedule.getId(), e);
                        }
                    }
                }, asyncReserveTransferRepeatExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private String find(Map<String, String> parent, String x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
        }
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent, parent.get(x)));
        }
        return parent.get(x);
    }

    private void union(Map<String, String> parent, String a, String b) {
        String rootA = find(parent, a);
        String rootB = find(parent, b);
        if (!rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }
}
