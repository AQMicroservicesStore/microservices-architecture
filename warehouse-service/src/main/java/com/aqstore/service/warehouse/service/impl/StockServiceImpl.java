package com.aqstore.service.warehouse.service.impl;

import org.springframework.stereotype.Service;

import com.aqstore.service.event.EventType;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiQuantityActionTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.WarehouseExceptionType;
import com.aqstore.service.warehouse.event.WarehouseEventSupplier;
import com.aqstore.service.warehouse.mapper.StockMapper;
import com.aqstore.service.warehouse.persistence.StockRepository;
import com.aqstore.service.warehouse.persistence.entity.Stock;
import com.aqstore.service.warehouse.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{
	private final StockRepository stockRepository;
	private final StockMapper stockMapper;
	private final WarehouseEventSupplier eventSupplier;

	
	@Override
	public ApiStockDto getStock(Long productId) {
		try {
			log.info("start to retrieve Stock of product with id: {}", productId);
			Stock stock = retrieveStock(productId);
			ApiStockDto response = stockMapper.toDTO(stock);
			return response;
		} catch (Exception e) {
			throw AQStoreExceptionHandler.handleException(e);
		}	
	}

	@Override
	public ApiStockDto updateStock(Long productId, ApiStockDto apiStockDto) {
		try {
			log.info("start to update Stock of product with id: {}", productId);
			Stock stockToUpdate = retrieveStock(productId);
			stockMapper.updateEntity(stockToUpdate,stockMapper.toEntity(apiStockDto));
			Stock stock = stockRepository.save(stockToUpdate);
			// send event message to : WarehouseConstants.STOCK_TOPIC
			eventSupplier.sendStockEvent(stock,EventType.UPDATED);
			ApiStockDto response = stockMapper.toDTO(stock);
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
			Stock stockToUpdate = retrieveStock(productId);	
			Integer quantity = stockToUpdate.getQuantity();
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
			stockToUpdate.setQuantity(quantity);
			Stock stock = stockRepository.save(stockToUpdate);
			// send event message to : WarehouseConstants.STOCK_TOPIC
			eventSupplier.sendStockEvent(stock,EventType.UPDATED);
			ApiStockDto response = stockMapper.toDTO(stock);
			return response;
		} catch (Exception e) {
			log.error("failed to update quantity stock with id  {} ",productId);
			throw AQStoreExceptionHandler.handleException(e);
		}	
	}

	private Stock retrieveStock(Long productId) {
		Stock stock =stockRepository.findById(productId).orElse(null);
		if(stock==null) {
			throw AQStoreExceptionHandler.handleException(WarehouseExceptionType.STOCK_NOT_FOUND, productId);
		}
		return stock;
	}

}
