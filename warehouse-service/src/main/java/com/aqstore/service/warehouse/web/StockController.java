package com.aqstore.service.warehouse.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.StockApi;
import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.WarehouseConstants;
import com.aqstore.service.warehouse.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(WarehouseConstants.WAREHOUSE_V1_PREFIX)
@RequiredArgsConstructor
public class StockController implements StockApi{
	private final ProductService productService;

	
	@Override
	public ResponseEntity<ApiStockDto> getStock(Long stockId) {
		ApiStockDto response = productService.getStock(stockId);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<ApiStockDto> updateStock(Long stockId, ApiStockDto apiStockDto) {
		ApiStockDto response = productService.updateStock(stockId, apiStockDto);
		return ResponseEntity.ok(response);
	}

	
	@Override
	public ResponseEntity<ApiStockDto> updateItemQuantity(Long stockId, Integer quantityToUpdate, ApiQuantityActionTypeDto action) {
		ApiStockDto response = productService.updateProductQuantity(stockId, quantityToUpdate, action);
		return ResponseEntity.ok(response);
	}
	
	

	
	
	
	
	
}
