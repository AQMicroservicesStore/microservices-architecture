package com.aqstore.service.order;

import org.springframework.http.HttpStatus;

import com.aqstore.service.exception.AQStoreExceptionType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderExceptionType implements AQStoreExceptionType{
	
	ORDER_NOT_FOUND("WP04","Order with id %s not found on database",HttpStatus.NOT_FOUND),
	INVALID_STOCK_QUANTITY("WS02","The quantity of stock relating to the productId:%S cannot be negated. "
			+ "The current value is %S. "
			+ "If %s isubtract the quantity becomes negative.",HttpStatus.BAD_REQUEST),
	UNAUTHORIZED_ROLE("WU01","Role %s - Unauthorized  to perform this operation",HttpStatus.UNAUTHORIZED),
	EVENT_FAILED("WES1","Error while send event to topic-> %s  with id %s ",HttpStatus.INTERNAL_SERVER_ERROR),
	
	;
	
	@Getter private final String code;
	@Getter private final String message;
	@Getter private final HttpStatus httpStatus;


	



}
