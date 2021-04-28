package com.aqstore.service.catalog.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.catalog.CatalogConstants;
import com.aqstore.service.catalog.service.SearchProductService;
import com.aqstore.service.openapi.ProductsApi;
import com.aqstore.service.openapi.model.ApiCatalogProductDto;
import com.aqstore.service.openapi.model.ApiCatalogProductsDto;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(CatalogConstants.API_V1_PREFIX)
@RequiredArgsConstructor
public class SearchProductController implements ProductsApi{
	private final SearchProductService searchProductService;
	
	
	@Override
	public ResponseEntity<ApiCatalogProductsDto> listproducts(Integer limit, Integer page, String filter) {
		ApiCatalogProductsDto response = searchProductService.listproducts(limit, page, filter);
		return ResponseEntity.ok(response);

	}
	
	@Override
	public ResponseEntity<ApiCatalogProductDto> showProductById(Long productId) {
		ApiCatalogProductDto response =  searchProductService.showProductById(productId);
		return ResponseEntity.ok(response);
	}
	

	
}
