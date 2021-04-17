package com.aqstore.service.catalog;

import org.springframework.http.HttpStatus;

import com.aqstore.service.exception.AQStoreExceptionType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CatalogExceptionType implements AQStoreExceptionType{
	
	PRODUCT_BAD_REQUEST("CP01","",HttpStatus.BAD_REQUEST),
	PRODUCT_ALREADY_EXISTS("CP02","Product with name %s already exists",HttpStatus.BAD_REQUEST),
	PRODUCT_CONFLICT_NAME("CP03","Product with id %s  has a different name :%s",HttpStatus.BAD_REQUEST),
	PRODUCT_NOT_FOUND("CP04","Product with id %s not found on database",HttpStatus.NOT_FOUND),
	UNAUTHORIZED_ROLE("WU01","Role %s - Unauthorized  to perform this operation",HttpStatus.UNAUTHORIZED),
	EVENT_FAILED("WES1","Error while send event to topic-> %s  with id %s ",HttpStatus.INTERNAL_SERVER_ERROR),
	PRODUCT_PAGE_BAD_REQUEST("CP01","the 'page' parameter passed in input is invalid, it cannot exceed the total pages",HttpStatus.BAD_REQUEST),

	;
	
	@Getter private final String code;
	@Getter private final String message;
	@Getter private final HttpStatus httpStatus;



	



}
