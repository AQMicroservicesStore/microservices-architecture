package com.aqstore.service.warehouse.config;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.EventSupplier;

@Configuration
public class BeanInitializer {

	
	@Bean
	public EventSupplier eventSupplier(final StreamBridge streamBridge) {
		return new EventSupplier(streamBridge);
	}
}
