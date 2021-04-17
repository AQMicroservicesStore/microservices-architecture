package com.aqstore.service.catalog.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Named("mapperConverter")
public class MapperConverter {

	
	@Named("longToInstant")
	public  Instant convertLongToInstant(Long value) {
		return Instant.ofEpochMilli(value);
	}
	
	@Named("longToLocalDateTime")
	public  LocalDateTime convertToLocalDateTime(Long value) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value),ZoneId.systemDefault());
	}
	
	
}
