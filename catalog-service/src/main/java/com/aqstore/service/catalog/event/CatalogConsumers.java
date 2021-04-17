package com.aqstore.service.catalog.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.aqstore.service.catalog.mapper.CatalogProductMapper;
import com.aqstore.service.catalog.persistence.entity.CatalogProduct;
import com.aqstore.service.catalog.service.SearchProductService;
import com.aqstore.service.event.EventWrapper;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogConsumers {

	private final SearchProductService productService;
	private final CatalogProductMapper productMapper;

	@Bean
	public Consumer<EventWrapper<Long, ProductEvent>> productConsumer() throws Exception {
		return event -> {
			try {
				log.info("[ProductConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getCreatedAt().toString(),event.getPayloadId());
				ProductEvent payloadEvent = event.getPayload();
				CatalogProduct product = productMapper.toEntity(payloadEvent);
				switch (payloadEvent.getEventType()) {
				case CREATED:
					productService.addProductToCatalog(product);
					break;
				case UPDATED:
					productService.updateCatalogProduct(product);
					break;
				case DELETED:
					productService.deleteCatalogProduct(product);
					break;
				default:
					break;
				}
				log.info("[ProductConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[ProductConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}

	@Bean
	public Consumer<EventWrapper<Long, StockEvent>> stockConsumer() throws Exception {
		return event -> {
			try {
				log.info("[StockConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getCreatedAt().toString(),event.getPayloadId());
				StockEvent payloadEvent = event.getPayload();
				CatalogProduct product = productMapper.toEntity(payloadEvent);
				switch (payloadEvent.getEventType()) {
				case CREATED:
				case UPDATED:
					productService.updateCatalogProduct(product);
					break;
				case DELETED:
					productService.deleteCatalogProduct(product);
					break;
				default:
					break;
				}
				log.info("[StockConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[StockConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}
		};

	}

}
