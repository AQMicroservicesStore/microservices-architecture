package com.aqstore.service.warehouse.mapper;

import java.time.Instant;
import java.util.UUID;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Named("mapperConverter")
public class MapperConverter {

	
	@Named("longToInstant")
	public  Instant convertLongToInstant(Long value) {
		return Instant.ofEpochMilli(value);
	}
	
	
	
	@Named("checkEmptyString")
	public  String checkEmptyString(String source) {
		return (StringUtils.hasText(source)) ? source : null;
	}
	
	@Named("stringToUUID")
	public  UUID stringToUUID(String source) {
		return (StringUtils.hasText(source)) ? UUID.fromString(source) : null;
	}
	
	@Named("UUIDToString")
	public  String UUIDToString(UUID source) {
		return (source!= null) ? source.toString() : null;
	}
	
	
}
