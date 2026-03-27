package bridge.bridge_bank.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncAccountingValidationExecutorConfig {

    @Bean(name = "asyncAccountingValidationExecutor")
    public Executor asyncAccountingValidationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(0);          // 기본 워커 수
        executor.setMaxPoolSize(3);           // 최대 워커 수
        executor.setQueueCapacity(18);       // 대기 큐
        executor.setThreadNamePrefix("accounting-validation-worker-");
        executor.setKeepAliveSeconds(15);

        // 큐가 꽉 찼을 때 호출한 쓰레드가 직접 수행 -> 무한 폭주 방지에 도움
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
