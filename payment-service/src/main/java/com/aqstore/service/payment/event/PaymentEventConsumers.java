package com.aqstore.service.payment.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentEventConsumers {
	private final PaymentEventHandler eventHandler;

	
	
	@Bean
	public Consumer<OrderPaymentsEvent> paymentConsumer() throws Exception {
		return event -> {
			try {
				log.info("[PaymentConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				
				case SAGA_CONTINUE:
					eventHandler.createPaymentRecordByOrder(event);
					break;
				case SAGA_ROLLBACK:
					eventHandler.refundPayment(event);
					break;
				default:
					break;
				}
				log.info("[PaymentConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[PaymentConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
	
	
	
	
}
