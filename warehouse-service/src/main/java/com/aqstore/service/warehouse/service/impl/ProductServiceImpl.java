package com.aqstore.service.warehouse.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aqstore.service.event.EventType;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.WarehouseExceptionType;
import com.aqstore.service.warehouse.event.WarehouseEventHandler;
import com.aqstore.service.warehouse.mapper.ProductMapper;
import com.aqstore.service.warehouse.mapper.StockMapper;
import com.aqstore.service.warehouse.persistence.ProductRepository;
import com.aqstore.service.warehouse.persistence.entity.Product;
import com.aqstore.service.warehouse.persistence.entity.Stock;
import com.aqstore.service.warehouse.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final StockMapper stockMapper;
	private final WarehouseEventHandler eventHandler;

	@Override
	@Transactional
	public ApiProductDto addProduct(ApiProductDto requestDto) {
		log.info("start to add product {}", requestDto.getProductName());
		try {
			checkAlreadyExists(requestDto.getProductName());
			Product productToSave = productMapper.toEntity(requestDto);
			Product product = productRepository.save(productToSave);
			// send event message to : WarehouseConstants.PRODUCT_TOPIC
			eventHandler.sendProductEvent(product, EventType.CQRS_CREATED);
			ApiProductDto response = productMapper.toDTO(product);
			return response;
		} catch (Exception e) {
			log.error("failed to add product");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiProductDto getProduct(Long productId) {
		try {
			log.info("start to retrieve product {}", productId);
			Product product = productRepository.findById(productId).orElseThrow(() -> {
				return AQStoreExceptionHandler.handleException(WarehouseExceptionType.PRODUCT_NOT_FOUND, productId);
			});
			ApiProductDto response = productMapper.toDTO(product);
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve product");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	
	@Override
	public ApiStockDto getStock(Long productId) {
		try {
			log.info("start to retrieve Stock of product with id: {}", productId);
			Product product =productRepository.findById(productId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(WarehouseExceptionType.STOCK_NOT_FOUND, productId));
			ApiStockDto response = stockMapper.toDTO(product.getStock());
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve stock with id  {} ",productId);
			throw AQStoreExceptionHandler.handleException(e);
		}	
	}

	@Override
	public ApiStockDto updateStock(Long productId, ApiStockDto apiStockDto) {
		try {
			log.info("start to update Stock of product with id: {}", productId);
			Product product = productRepository.findById(productId).orElseThrow(
				() -> AQStoreExceptionHandler.handleException(WarehouseExceptionType.STOCK_NOT_FOUND, productId));
			
			Stock stockToUpdate = product.getStock();
			productMapper.updateStockEntity(stockToUpdate, apiStockDto);
			product.setStock(stockToUpdate);
			ApiStockDto response = updateProduct(product);
			return response;
		} catch (Exception e) {
			log.error("failed to update stock with id  {} ",productId);
			throw AQStoreExceptionHandler.handleException(e);
		}	
	}

	@Override
	public ApiStockDto updateProductQuantity(Long productId, Integer quantityToUpdate, ApiQuantityActionTypeDto action) {
		try {
			log.info("start to update Stock quantity of product with id: {} - Action : {}", productId,action);
			Product product = productRepository.findById(productId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(WarehouseExceptionType.STOCK_NOT_FOUND, productId));
			Integer quantity = product.getStock().getQuantity();
			switch(action) {
			case ADD:
				quantity+= quantityToUpdate;
				break;
			case REMOVE:
				quantity-=quantityToUpdate;
				break;
			}
			if(quantity<0) {
				AQStoreExceptionHandler.handleException(WarehouseExceptionType.INVALID_STOCK_QUANTITY,productId,quantityToUpdate,quantity);
			}	
			product.getStock().setQuantity(quantity);
			ApiStockDto response = updateProduct(product);
			return response;
		} catch (Exception e) {
			log.error("failed to update quantity stock with id  {} ",productId);
			throw AQStoreExceptionHandler.handleException(e);
		}	
	}
	

	
	private ApiStockDto updateProduct(Product product) {
		Product productUpdated = productRepository.save(product);
		// send event message to : WarehouseConstants.STOCK_TOPIC
		eventHandler.sendProductEvent(productUpdated,EventType.CQRS_CREATED);
		return stockMapper.toDTO(productUpdated.getStock());
	}
	
	
	
	
	private void checkAlreadyExists(String name) {
		if (productRepository.existsByName(name)) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.PRODUCT_ALREADY_EXISTS, name);
		}
	}
	
	
}
