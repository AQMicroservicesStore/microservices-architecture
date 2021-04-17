package com.aqstore.service.warehouse.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.StockApi;
import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.WarehouseConstants;
import com.aqstore.service.warehouse.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(WarehouseConstants.WAREHOUSE_V1_PREFIX)
@RequiredArgsConstructor
public class StockController implements StockApi{
	private final StockService stockService;

	
	@Override
	public ResponseEntity<ApiStockDto> getStock(Long productId) {
		ApiStockDto response = stockService.getStock(productId);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<ApiStockDto> updateStock(Long productId, ApiStockDto apiStockDto) {
		ApiStockDto response = stockService.updateStock(productId, null);
		return ResponseEntity.ok(response);
	}

	
	@Override
	public ResponseEntity<ApiStockDto> updateItemQuantity(Long productId, Integer quantityToUpdate, ApiQuantityActionTypeDto action) {
		ApiStockDto response = stockService.updateProductQuantity(productId, quantityToUpdate, action);
		return ResponseEntity.ok(response);
	}
	
	

	
	
	
	
	
}
