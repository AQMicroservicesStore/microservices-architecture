package com.aqstore.service.account.event;

import java.util.ArrayList;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.account.mapper.AddressMapper;
import com.aqstore.service.account.persistence.AddressRepository;
import com.aqstore.service.account.persistence.entity.Address;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderCheckAddressEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.event.payload.component.EventUserAddressDTO;
import com.aqstore.service.exception.AQStoreExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountEventHandler {
	private final AddressRepository addressRepository;
	private final AddressMapper addressMapper;

	@Bean
	public Function<OrderCheckAddressEvent, OrderSagaEvent> orderCheckAddress() {
		return event -> {
			try {
				log.info("[OrderCheckAddress] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]",
						event.getEventId(), event.getEventCreationTimestamp().toString(), event.getPayloadId());
				switch (event.getEventType()) {
				case SAGA_CONTINUE:
					return retrieveUserAddress(event);
				default:
					return defaultPayload(event, EventType.IGNORE);
				}
			} catch (Exception e) {
				log.error("[OrderCheckAddress] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
				return defaultPayload(event, EventType.SAGA_ERROR_ROLLBACK);
			}
		};
	}

	private OrderSagaEvent defaultPayload(OrderCheckAddressEvent payload, EventType type) {
		return OrderSagaEvent.builder().eventType(type).stepInfo(CreateOrderSagaStep.STEP3_CHECK_ADDRESS)
				.orderId(payload.getOrderId()).orderItems(new ArrayList<>()).errorMessages("").build();
	}

	private OrderSagaEvent retrieveUserAddress(OrderCheckAddressEvent payload) {
		OrderSagaEvent sagaEvent = defaultPayload(payload, EventType.SAGA_CONTINUE);
		Address userAddress = addressRepository.findById(payload.getUserId()).orElse(null);
		if (userAddress == null) {
			sagaEvent.setErrorMessages("user address not found");
			sagaEvent.setEventType(EventType.SAGA_ROLLBACK);
			return sagaEvent;
		}
		EventUserAddressDTO eventAddress = addressMapper.toEventDto(userAddress);
		sagaEvent.setUserAddress(eventAddress);
		return sagaEvent;
	}

}

/*
 * SAGA 1) send event to availability products and wait . update orderHistory
 * ----> createOrder -our 2) send event to check userAddress and wait . update
 * orderHistory ----> orderCheckAddress - in - out 3) sendEvent to unlock
 * payments . update orderHistory ----> orderUnlockPayments - in-out 4) wait
 * paymentResponse . unlock deliveryService . updateOrderHistory 5) wait
 * deliveryConfirmed . updateOrderHistory. 6) wait deliveryArrived .
 * updateOrderHistory; --> handle user address Missing --> handle availability
 * products --> handle payment refused --> handle shipping lost -> refund
 * payment
 * 
 */

// #  ->   
// source = #createorder - updateOrderHistory 
// processor = # orderCheckAddress -> orderUnlockPayment -> orderUnlockDeliveryService ->
// consumer
// consumer = deliveryConsumer