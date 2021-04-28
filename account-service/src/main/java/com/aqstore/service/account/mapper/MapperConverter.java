package com.aqstore.service.account.mapper;

import java.time.Instant;

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
	
}
