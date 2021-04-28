package com.aqstore.service.payment.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MyAuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		String username = "EVENT_SOURCING";
		try {
	         username = SecurityContextHolder.getContext().getAuthentication().getName();
			if(!StringUtils.hasText(username)) {
				username = "VOID";
			}
		}catch (Exception e) {
			log.info("invalid auditor ");
		}
		
		
        return Optional.of(username);
	}

}