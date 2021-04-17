package com.aqstore.service.warehouse.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aqstore.service.IAQStoreControllerAdvice;
import com.aqstore.service.exception.model.AQStoreErrorModel;
import com.aqstore.service.openapi.model.ApiErrorDto;

@RestControllerAdvice
public class WarehouseControllerAdvice implements IAQStoreControllerAdvice<ApiErrorDto>{

	
	
	
	
	@Override
	public ApiErrorDto convertToDTO(AQStoreErrorModel error) {
		
		return new ApiErrorDto()
				.errorCode(error.getErrorCode())
				.errors(error.getErrors())
				.datetime(error.getDatetime());
	}

	
	
}
