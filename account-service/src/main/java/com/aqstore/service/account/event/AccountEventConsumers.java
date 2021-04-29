package com.aqstore.service.account.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.OrderCheckAddressEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountEventConsumers {
	private final AccountEventHandler eventHandler;
	
	@Bean
	public Consumer<OrderCheckAddressEvent> orderCheckAddressConsumer() {
		return event -> {
			try {
				log.info("[OrderCheckAddress] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]",
						event.getEventId(), event.getEventCreationTimestamp().toString(), event.getPayloadId());
				switch (event.getEventType()) {
				case SAGA_CONTINUE:
					eventHandler.retrieveUserAddress(event);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				log.error("[OrderCheckAddress] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}
		};
	}

	
	
	
	
	
	
	
}
