package com.dyslexia.dyslexia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 코어 풀 크기 증가 (기본 작업 스레드 수)
        executor.setCorePoolSize(20);
        
        // 최대 풀 크기 설정 (최대 동시 실행 가능한 스레드 수)
        executor.setMaxPoolSize(50);
        
        // 큐 용량 증가 (대기 작업 큐 크기)
        executor.setQueueCapacity(100);
        
        // 스레드 이름 접두사 설정
        executor.setThreadNamePrefix("AsyncTask-");
        
        // 우아한 종료 대기 시간 (초)
        executor.setAwaitTerminationSeconds(60);
        
        // 풀 종료 전 대기 작업 처리
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 거부 정책 설정 - CallerRunsPolicy: 호출자 스레드에서 실행
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
} 