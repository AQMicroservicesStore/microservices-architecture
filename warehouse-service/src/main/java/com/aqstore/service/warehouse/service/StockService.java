package com.aqstore.service.warehouse.service;

import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;

public interface StockService {

	ApiStockDto getStock(Long productId);

	ApiStockDto updateStock(Long productId, ApiStockDto apiStockDto);

	ApiStockDto updateProductQuantity(Long productId, Integer quantityToUpdate, ApiQuantityActionTypeDto action);

}
