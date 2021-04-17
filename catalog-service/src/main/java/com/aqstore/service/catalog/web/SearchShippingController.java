package com.aqstore.service.catalog.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.catalog.CatalogConstants;
import com.aqstore.service.catalog.service.SearchShippingService;
import com.aqstore.service.openapi.ShippingsApi;
import com.aqstore.service.openapi.model.ApiCatalogShippingMethodsDto;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(CatalogConstants.API_V1_PREFIX)
@RequiredArgsConstructor
public class SearchShippingController implements ShippingsApi{
	private final SearchShippingService searchShippingService;
	
	
	@Override
	public ResponseEntity<ApiCatalogShippingMethodsDto> listshippings() {
		return searchShippingService.listShippingMethods();
	}
	
	
}
