package com.aqstore.service.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aqstore.service.catalog.CatalogConstants;

@Configuration
public class BeanInitializer {

	@Bean(CatalogConstants.EVENT_TASK_EXECUTOR)
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(100);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("CAsyncEvent-");
		return executor;
	}
	
	
	
	
}
