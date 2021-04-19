package com.aqstore.service.warehouse.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;


public class MyAuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(!StringUtils.hasText(username)) {
			username = "TEST";
		}
        return Optional.of(username);
	}

}