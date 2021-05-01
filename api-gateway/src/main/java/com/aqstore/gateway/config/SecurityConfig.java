package com.aqstore.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
public class SecurityConfig {

	@Autowired
	private ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
        	.authorizeExchange()
        	.pathMatchers("/private/**").permitAll()
        	.pathMatchers("/public/**").authenticated()
        	.and()
        	.oauth2Login(oauth2->oauth2.clientRegistrationRepository(clientRegistrationRepository))
        		.logout(logout->logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));

//        	.oauth2ResourceServer()
//        	.jwt();
        	return http.build();
	}

//	return http.csrf().disable().authorizeExchange(exchange->exchange.matchers(EndpointRequest.to("/private/**")).permitAll().matchers(EndpointRequest.to("/public/**")).authenticated())
//	.oauth2Login(oauth2->oauth2.clientRegistrationRepository(clientRegistrationRepository)).logout(logout->logout.logoutSuccessHandler(oidcLogoutSuccessHandler()))
//                .build();
//    }

	private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
				new OidcClientInitiatedServerLogoutSuccessHandler(this.clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
		return oidcLogoutSuccessHandler;
	}

}