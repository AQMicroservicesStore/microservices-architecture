package com.aqstore.service.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "db")
public class DbProperties {

	private String username;
	private String password;
	private Integer port;
	private String host;
	private String schema;
	private String endpoint;

	
}
