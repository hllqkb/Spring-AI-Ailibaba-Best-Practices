package com.springai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 使用@Configuration注解表明这是一个配置类
@Configuration
// 使用@EnableAsync注解启用Spring的异步方法支持
@EnableAsync
public class AsyncConfig implements WebMvcConfigurer {

    // 使用@Bean注解将ThreadPoolTaskExecutor注册为Spring容器中的一个Bean
    @Bean
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置线程池的核心线程数，核心线程会一直存活
        executor.setCorePoolSize(10);
        // 设置线程池的最大线程数，当任务队列满了并且核心线程都在工作时，会创建新线程
        executor.setMaxPoolSize(50);
        // 设置线程池的队列容量，当核心线程都在工作时，新来的任务会放入队列中等待
        executor.setQueueCapacity(500);
        // 设置线程名称前缀，方便识别线程
        executor.setThreadNamePrefix("AsyncThread-");
        // 设置在关闭线程池时，是否等待完成所有任务
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置在关闭线程池时，等待所有任务完成的最大等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 初始化线程池
        executor.initialize();
        return executor;
    }

    // 重写WebMvcConfigurer接口的configureAsyncSupport方法，配置异步支持
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 使用上面定义的ThreadPoolTaskExecutor来处理异步任务
        configurer.setTaskExecutor(asyncTaskExecutor());
        // 设置异步请求的默认超时时间（毫秒），这里设置为30秒
        configurer.setDefaultTimeout(30000); // 30 seconds timeout
    }
}
