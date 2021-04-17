package com.aqstore.service.warehouse.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;


public class MyAuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of("TEST");
	}

}