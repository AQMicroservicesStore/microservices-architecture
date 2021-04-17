package com.aqstore.service.catalog.service;

import org.springframework.http.ResponseEntity;

import com.aqstore.service.openapi.model.ApiCatalogShippingMethodsDto;

public interface SearchShippingService {

	ResponseEntity<ApiCatalogShippingMethodsDto> listShippingMethods();
}
