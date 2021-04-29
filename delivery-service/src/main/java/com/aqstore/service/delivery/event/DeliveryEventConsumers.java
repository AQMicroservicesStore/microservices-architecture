package com.aqstore.service.delivery.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.OrderDeliveryEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeliveryEventConsumers {
	private final DeliveryEventHandler eventHandler;

	
	
	@Bean
	public Consumer<OrderDeliveryEvent> deliveryConsumer() throws Exception {
		return event -> {
			try {
				log.info("[DeliveryConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				
				case SAGA_CONTINUE:
					eventHandler.createDeliveryRecordByOrder(event);
					break;
				default:
					break;
				}
				log.info("[DeliveryConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[DeliveryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
}