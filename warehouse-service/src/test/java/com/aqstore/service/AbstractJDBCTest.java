package com.aqstore.service;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public abstract class AbstractJDBCTest {
	
    private static final PostgreSQLContainer<?> postgresql;
    
    static {
    postgresql = new PostgreSQLContainer<> (DockerImageName.parse("postgres:13"))
    		.withExposedPorts(5736);
    postgresql.start();
    }
    


    
    
    
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
      	registry.add("db.username", postgresql::getUsername);
    	registry.add("db.password", postgresql::getPassword);
    	registry.add("db.port", postgresql::getFirstMappedPort);
    	registry.add("db.host", postgresql::getHost);
    	registry.add("db.schema", postgresql::getDatabaseName);
    	registry.add("db.endpoint", AbstractJDBCTest::dbEndpoint);
        registry.add("spring.datasource.url", postgresql::getJdbcUrl);
        registry.add("spring.datasource.username", postgresql::getUsername);
        registry.add("spring.datasource.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
        registry.add("spring.flyway.user", postgresql::getUsername);
        registry.add("spring.flyway.password", postgresql::getPassword);
    }



    
    private static String dbEndpoint() {
    	return String.format("postgresql://%s:%s/%s", postgresql.getHost(),
                postgresql.getFirstMappedPort(), postgresql.getDatabaseName());    
    }


    
    
    
}
