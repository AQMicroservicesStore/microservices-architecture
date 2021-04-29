package com.aqstore.service.catalog.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.aqstore.service.catalog.CatalogConstants;
import com.aqstore.service.catalog.mapper.CatalogProductMapper;
import com.aqstore.service.catalog.persistence.entity.CatalogProduct;
import com.aqstore.service.catalog.service.SearchProductService;
import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogEventHandler {

	private final SearchProductService productService;
	private final CatalogProductMapper productMapper;

	
	@Async(CatalogConstants.EVENT_TASK_EXECUTOR)
	public void addProductToCatalog(ProductEvent event) {
		try {
			CatalogProduct product = productMapper.toEntity(event);
			productService.addProductToCatalog(product);

		}catch (Exception e) {
			log.error("[ProductConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}
		
	}
	
	@Async(CatalogConstants.EVENT_TASK_EXECUTOR)
	public void updateCatalogProduct(ProductEvent event) {
		try {
			CatalogProduct product = productMapper.toEntity(event);
			productService.updateCatalogProduct(product);

		}catch (Exception e) {
			log.error("[ProductConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}		
	}
	@Async(CatalogConstants.EVENT_TASK_EXECUTOR)
	public void deleteCatalogProduct(ProductEvent event) {
		try {
			CatalogProduct product = productMapper.toEntity(event);
			productService.deleteCatalogProduct(product);
		}catch (Exception e) {
			log.error("[ProductConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}		
		
	}

}
