package com.aqstore.service.warehouse.service;

import com.aqstore.service.openapi.model.ApiProductDto;

public interface ProductService {

	ApiProductDto addProduct(ApiProductDto apiProductDto);

	ApiProductDto updateProduct(Long productId, ApiProductDto apiProductDto);

	ApiProductDto getProduct(Long productId);

}
