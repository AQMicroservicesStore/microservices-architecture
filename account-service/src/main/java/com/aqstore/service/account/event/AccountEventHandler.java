package com.aqstore.service.account.event;

import java.util.ArrayList;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aqstore.service.account.AccountConstants;
import com.aqstore.service.account.mapper.AddressMapper;
import com.aqstore.service.account.persistence.AddressRepository;
import com.aqstore.service.account.persistence.entity.Address;
import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderCheckAddressEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.event.payload.component.EventUserAddressDTO;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.exception.AbstractAQStoreException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountEventHandler {
	private final AddressRepository addressRepository;
	private final AddressMapper addressMapper;
	private final EventSupplier eventSupplier;

	public void sendOrderSagaEvent(OrderSagaEvent eventPayload) {
		EventProducerWrapper<OrderSagaEvent> eventWrapper = new EventProducerWrapper<>(AccountConstants.CREATE_ORDER_SAGA_TOPIC,
				eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}

	@Async(AccountConstants.EVENT_TASK_EXECUTOR)
	public void retrieveUserAddress(OrderCheckAddressEvent payload) {
		try {
			OrderSagaEvent sagaEvent = defaultPayload(payload, EventType.SAGA_CONTINUE);
			Address userAddress = addressRepository.findById(payload.getUserId()).orElse(null);
			if (userAddress == null) {
				sagaEvent.setErrorMessages("user address not found");
				sagaEvent.setEventType(EventType.SAGA_ROLLBACK);
			}else {
				EventUserAddressDTO eventAddress = addressMapper.toEventDto(userAddress);
				sagaEvent.setUserAddress(eventAddress);			
			}
			sendOrderSagaEvent(sagaEvent);
		}catch (Exception e) {
			log.error("[OrderCheckAddress] : failed to consume event with Id=[{}]", payload.getEventId());
			sendOrderSagaRollbackByException(payload, AQStoreExceptionHandler.handleException(e));
		}
		
	}


	private OrderSagaEvent defaultPayload(OrderCheckAddressEvent payload, EventType type) {
		return OrderSagaEvent.builder().eventType(type).stepInfo(CreateOrderSagaStep.STEP3_CHECK_ADDRESS)
				.orderId(payload.getOrderId()).orderItems(new ArrayList<>()).errorMessages("").build();
	}
	
	
	
	
	private void sendOrderSagaRollbackByException( OrderCheckAddressEvent event, AbstractAQStoreException e) {
		OrderSagaEvent eventPayload = getSagaEventByException(event ,e);
		sendOrderSagaEvent(eventPayload);
	}
	
	private OrderSagaEvent getSagaEventByException(OrderCheckAddressEvent event, AbstractAQStoreException e) {
		return OrderSagaEvent.builder()
				.errorMessages(e.getMessage())
				.orderId(event.getOrderId())
				.eventStepId(null)
				.eventStepDate(null)
				.stepInfo(CreateOrderSagaStep.STEP3_CHECK_ADDRESS)
				.eventType(EventType.SAGA_ROLLBACK)
				.build();
	}

	
	
	
}
