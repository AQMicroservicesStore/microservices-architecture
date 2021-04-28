package com.aqstore.service.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;





@Configuration
@EnableJdbcAuditing
public class MyDataSourceConfig {

	@Bean
	public AuditorAware<String> auditProvider() {
		return new MyAuditorAwareImpl();
	}
	

	
	
}
