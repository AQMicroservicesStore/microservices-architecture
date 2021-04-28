package com.aqstore.service.warehouse.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.ProductApi;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.warehouse.WarehouseConstants;
import com.aqstore.service.warehouse.service.ProductService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(WarehouseConstants.WAREHOUSE_V1_PREFIX)
@RequiredArgsConstructor
public class ProductController implements ProductApi{
	private final ProductService productService;
	
	
	@Override
	public ResponseEntity<ApiProductDto> getProductById(Long productId) {
		ApiProductDto response =  productService.getProduct(productId);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<ApiProductDto> addProduct(ApiProductDto apiProductDto) {
		ApiProductDto response =  productService.addProduct(apiProductDto);
		return ResponseEntity.ok(response);
	}

	
	
}
