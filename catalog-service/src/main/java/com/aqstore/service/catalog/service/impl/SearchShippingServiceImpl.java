package com.aqstore.service.catalog.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aqstore.service.catalog.service.SearchShippingService;
import com.aqstore.service.openapi.model.ApiCatalogShippingMethodsDto;

@Service
public class SearchShippingServiceImpl implements SearchShippingService{

	@Override
	public ResponseEntity<ApiCatalogShippingMethodsDto> listShippingMethods() {
		// TODO Auto-generated method stub
		return null;
	}

}
