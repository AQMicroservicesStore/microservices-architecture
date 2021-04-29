package com.aqstore.service.order.config;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.order.OrderConstants;

@Configuration
public class BeanInitializer {

	
	@Bean
	public EventSupplier eventSupplier(final StreamBridge streamBridge) {
		return new EventSupplier(streamBridge);
	}

	@Bean(OrderConstants.EVENT_TASK_EXECUTOR)
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(100);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("OAsyncEvent-");
		return executor;
	}



}

