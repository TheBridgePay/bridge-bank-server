package bridge.bridge_bank.domain.reserve_transfer_execution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ReserveTransferExecutorConfig {
    @Bean(name = "asyncReserveTransferOnceExecutor")
    public Executor asyncReserveOnceTransferExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);          // 기본 워커 수
        executor.setMaxPoolSize(8);           // 최대 워커 수
        executor.setQueueCapacity(10000);       // 대기 큐
        executor.setThreadNamePrefix("reserve-once-transfer-worker-");
        executor.setKeepAliveSeconds(15);

        // 큐가 꽉 찼을 때 호출한 쓰레드가 직접 수행 -> 무한 폭주 방지에 도움
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    @Bean(name = "asyncReserveTransferRepeatExecutor")
    public Executor asyncReserveTransferRepeatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);          // 기본 워커 수
        executor.setMaxPoolSize(8);           // 최대 워커 수
        executor.setQueueCapacity(10000);       // 대기 큐
        executor.setThreadNamePrefix("reserve-repeat-transfer-worker-");
        executor.setKeepAliveSeconds(15);

        // 큐가 꽉 찼을 때 호출한 쓰레드가 직접 수행 -> 무한 폭주 방지에 도움
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
