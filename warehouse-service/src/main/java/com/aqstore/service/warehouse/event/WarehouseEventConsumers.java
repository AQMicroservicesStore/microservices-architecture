package com.aqstore.service.warehouse.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.OrderCheckItemEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WarehouseEventConsumers {
	private final WarehouseEventHandler eventHandler;

	@Bean
	public Consumer<OrderCheckItemEvent> orderCheckItemsConsumer() {
		return event -> {
			try {
				log.info("[OrderCheckItems] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]",
						event.getEventId(), event.getEventCreationTimestamp().toString(), event.getPayloadId());
				switch (event.getEventType()) {
				case SAGA_ROLLBACK:
					eventHandler.rollbackProductsQuantity(event);
					break;
				case SAGA_CONTINUE:
					eventHandler.checkProductsQuantity(event);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				log.error("[OrderCheckItems] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}
		};
	}

}
