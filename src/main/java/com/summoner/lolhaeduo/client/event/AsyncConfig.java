package com.summoner.lolhaeduo.client.event;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);                // Thread pool에서 기본적으로 유지되는 스레드의 개수
        executor.setMaxPoolSize(5);                // Thread pool에서 사용할 수 있는 최대 스레드의 개수
        executor.setQueueCapacity(200);             // 작업 큐가 최대로 가질 수 있는 작업 개수
        executor.setThreadNamePrefix("custom-");    // 생성되는 스레드 이름 커스텀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); // 작업 큐가 가득 찬 상태에서 작업이 추가될 때 발생하는 예외
        executor.initialize();
        return executor;
    }
}
