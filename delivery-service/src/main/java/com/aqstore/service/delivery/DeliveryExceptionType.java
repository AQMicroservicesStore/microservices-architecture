package com.aqstore.service.delivery;

import org.springframework.http.HttpStatus;

import com.aqstore.service.exception.AQStoreExceptionType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeliveryExceptionType implements AQStoreExceptionType{
	
	DELIVERY_NOT_FOUND("D001","Delivery with order id %s not found on database",HttpStatus.NOT_FOUND),
	UNAUTHORIZED_ROLE("DU01","Role %s - Unauthorized  to perform this operation",HttpStatus.UNAUTHORIZED),
	EVENT_FAILED("DES1","Error while send event to topic-> %s  with id %s ",HttpStatus.INTERNAL_SERVER_ERROR),
	
	;
	
	@Getter private final String code;
	@Getter private final String message;
	@Getter private final HttpStatus httpStatus;


	



}
