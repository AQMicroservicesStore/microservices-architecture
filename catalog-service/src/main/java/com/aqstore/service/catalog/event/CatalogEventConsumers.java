package com.aqstore.service.catalog.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class CatalogEventConsumers {
	private final CatalogEventHandler eventHandler;
	
	
	
	@Bean
	public Consumer<ProductEvent> productConsumer() throws Exception {
		return event -> {
			try {
				log.info("[ProductConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				case CQRS_CREATED:
					eventHandler.addProductToCatalog(event);
					break;
				case CQRS_UPDATED:
					eventHandler.updateCatalogProduct(event);
					break;
				case CQRS_DELETED:
					eventHandler.deleteCatalogProduct(event);
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
