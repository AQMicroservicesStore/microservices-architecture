package com.aqstore.service.warehouse.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aqstore.service.event.EventType;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.warehouse.WarehouseExceptionType;
import com.aqstore.service.warehouse.event.WarehouseEventSupplier;
import com.aqstore.service.warehouse.mapper.ProductMapper;
import com.aqstore.service.warehouse.persistence.ProductRepository;
import com.aqstore.service.warehouse.persistence.StockRepository;
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
	private final StockRepository stockRepository;
	private final ProductMapper productMapper;
	private final WarehouseEventSupplier eventSupplier;

	@Override
	@Transactional
	public ApiProductDto addProduct(ApiProductDto requestDto) {
		log.info("start to add product {}", requestDto.getProductName());
		try {
			checkAlreadyExists(requestDto.getProductName());
			Product productToSave = productMapper.toEntity(requestDto);
			Product product = productRepository.save(productToSave);
			// create stock related to product
			Stock stockToSave = productMapper.toStockEntityNewRequest(requestDto, product.getId());
			Stock stock = stockRepository.save(stockToSave);			
			// send event message to : WarehouseConstants.PRODUCT_TOPIC
			eventSupplier.sendProductEvent(product,stock, EventType.CREATED);
			ApiProductDto response = productMapper.toDTO(product,stock);
			return response;
		} catch (Exception e) {
			log.error("failed to add product");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiProductDto updateProduct(Long productId, ApiProductDto requestDto) {
		try {
			log.info("start to update product {}", productId);
			// update product
			Product productToUpdate = retrieveProduct(productId, requestDto.getProductName());
			productMapper.updateEntity(productToUpdate, productMapper.toEntity(requestDto));
			Product product = productRepository.save(productToUpdate);

			// update stock related to product
			Stock stockToUpdate = retrieveStock(productId);
			productMapper.updateStockEntity(stockToUpdate, requestDto.getStock());
			Stock stock = stockRepository.save(stockToUpdate);
			// send event message to : WarehouseConstants.PRODUCT_TOPIC
			eventSupplier.sendProductEvent(product,stock, EventType.UPDATED);
			ApiProductDto response = productMapper.toDTO(product,stock);
			return response;
		} catch (Exception e) {
			log.error("failed to update product");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiProductDto getProduct(Long productId) {
		try {
			log.info("start to retrieve product {}", productId);
			Product product = retrieveProduct(productId);
			Stock stock = retrieveStock(productId);
			ApiProductDto response = productMapper.toDTO(product,stock);
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve product");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	
	
	
	private void checkAlreadyExists(String name) {
		if (productRepository.existsByName(name)) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.PRODUCT_ALREADY_EXISTS, name);
		}
	}
	

	private Product retrieveProduct(Long productId, String productName) {
		Product product = retrieveProduct(productId);
		if (!product.getName().equals(productName)) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.PRODUCT_CONFLICT_NAME, productId,
					productName);
		}

		return product;
	}

	private Product retrieveProduct(Long productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.PRODUCT_NOT_FOUND, productId);
		}
		return product;
	}
	
	private Stock retrieveStock(Long productId) {
		Stock stock =stockRepository.findById(productId).orElse(null);
		if(stock==null) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.STOCK_NOT_FOUND, productId);
		}
		return stock;
	}

	
	
	
}
