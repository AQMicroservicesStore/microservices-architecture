package com.aqstore.service.warehouse.event;

import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.EventWrapper;
import com.aqstore.service.warehouse.WarehouseConstants;
import com.aqstore.service.warehouse.mapper.ProductMapper;
import com.aqstore.service.warehouse.mapper.StockMapper;
import com.aqstore.service.warehouse.persistence.entity.Product;
import com.aqstore.service.warehouse.persistence.entity.Stock;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WarehouseEventSupplier  {
	private final ProductMapper productMapper;
	private final StockMapper stockMapper;
	private final EventSupplier eventSupplier;

	public void sendProductEvent(Product product, Stock stock, EventType type) {
		ProductEvent eventPayload = productMapper.toEvent(product, stock, type);
		EventWrapper<Long,ProductEvent> eventWrapper = new EventWrapper<>(WarehouseConstants.PRODUCT_TOPIC, eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}
	
	public void sendStockEvent(Stock stock, EventType type){
		StockEvent eventPayload = stockMapper.toEvent(stock, type);
		EventWrapper<Long,StockEvent> eventWrapper = new EventWrapper<>(WarehouseConstants.PRODUCT_TOPIC, eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);

	}
	
	
}
