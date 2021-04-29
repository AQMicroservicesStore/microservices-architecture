package com.aqstore.service.order.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestratorConsumer {
	private final OrderSagaOrchestratorHandler eventHandler;
	
	
	
	@Bean
	public Consumer<OrderSagaEvent> orderSagaConsumer() throws Exception {
		return event -> {
			try {
				log.info("[OrderSagaConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch(event.getEventType()) {
				case IGNORE:
					break;
				case SAGA_ERROR_ROLLBACK:
					eventHandler.handleRollbackError(event);
					break;	
				case SAGA_ROLLBACK:
					eventHandler.rollbackOrder(event);
					break;
				case SAGA_CONTINUE:
					eventHandler.continueSagaOrder(event);			
					break;
				default:
					break;
				}
			}catch(Exception e) {
				log.error("[OrderSagaConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
