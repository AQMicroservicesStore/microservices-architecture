package com.aqstore.service.warehouse.service;

import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;

public interface ProductService {

	ApiProductDto addProduct(ApiProductDto apiProductDto);

	ApiProductDto getProduct(Long productId);
	
	ApiStockDto getStock(Long productId);

	ApiStockDto updateProductQuantity(Long productId, Integer quantityToUpdate, ApiQuantityActionTypeDto action);

	ApiStockDto updateStock(Long productId, ApiStockDto apiStockDto);


}
