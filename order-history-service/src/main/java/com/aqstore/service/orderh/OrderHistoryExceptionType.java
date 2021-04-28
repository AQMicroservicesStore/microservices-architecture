package com.aqstore.service.orderh;

import org.springframework.http.HttpStatus;

import com.aqstore.service.exception.AQStoreExceptionType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderHistoryExceptionType implements AQStoreExceptionType{
	
	ORDER_ALREADY_EXISTS("OH01","Order with id %s already exists",HttpStatus.BAD_REQUEST),
	ORDER_NOT_FOUND("OH02","order with id %s not found on database",HttpStatus.NOT_FOUND),
	UNAUTHORIZED_ROLE("WU01","Role %s - Unauthorized  to perform this operation",HttpStatus.UNAUTHORIZED),
	ORDER_PAGE_BAD_REQUEST("OH03","the 'page' parameter passed in input is invalid, it cannot exceed the total pages",HttpStatus.BAD_REQUEST),

	;
	
	@Getter private final String code;
	@Getter private final String message;
	@Getter private final HttpStatus httpStatus;



	



}
