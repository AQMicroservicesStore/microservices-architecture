package com.aqstore.service.catalog.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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

	@Bean
	public Consumer<ProductEvent> productConsumer() throws Exception {
		return event -> {
			try {
				log.info("[ProductConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				CatalogProduct product = productMapper.toEntity(event);
				switch (event.getEventType()) {
				case CQRS_CREATED:
					productService.addProductToCatalog(product);
					break;
				case CQRS_UPDATED:
					productService.updateCatalogProduct(product);
					break;
				case CQRS_DELETED:
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

}
