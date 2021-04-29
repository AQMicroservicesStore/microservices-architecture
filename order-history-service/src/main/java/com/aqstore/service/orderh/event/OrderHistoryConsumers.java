package com.aqstore.service.orderh.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderHistoryConsumers {
	private final OrderHistoryEventHandler eventHandler;
	
	

	
	@Bean
	public Consumer<OrderHistoryEvent> orderHistoryConsumer() throws Exception {
		return event -> {
			try {
				log.info("[OrderHistoryConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				case CQRS_CREATED:
					eventHandler.addNewOrder(event);
					break;
				case CQRS_UPDATED:
					eventHandler.updateOrder(event);
					break;
				case CQRS_DELETED:
					eventHandler.updateOrder(event);
					break;
				default:
					break;
				}
				log.info("[OrderHistoryConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[OrderHistoryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
	
	
	
	
}
