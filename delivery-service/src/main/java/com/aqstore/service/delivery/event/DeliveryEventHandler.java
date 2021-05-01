package com.aqstore.service.delivery.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aqstore.service.delivery.DeliveryConstants;
import com.aqstore.service.delivery.mapper.DeliveryMapper;
import com.aqstore.service.delivery.persistence.DeliveryRepository;
import com.aqstore.service.delivery.persistence.entity.OrderDelivery;
import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderDeliveryEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiDeliveryStatusDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryEventHandler {
	private final EventSupplier eventSupplier;
	private final DeliveryRepository deliveryRepository;
	private final DeliveryMapper deliveryMapper;
	
	public void sendDeliveryShippedSagaEvent(OrderDelivery deliveryUpdated) {
		OrderSagaEvent eventPayload = getSagaEvent(deliveryUpdated,CreateOrderSagaStep.STEP5_ORDER_SHIPPED,EventType.SAGA_CONTINUE);
		sendOrderSagaEvent(eventPayload);
	}
	
	public void sendDeliveryLostSagaEvent(OrderDelivery deliveryUpdated) {
		OrderSagaEvent eventPayload = getSagaEvent(deliveryUpdated,CreateOrderSagaStep.STEP6_ORDER_DELIVERED,EventType.SAGA_ROLLBACK);
		sendOrderSagaEvent(eventPayload);
	}
	public void sendDeliveryArrivedSagaEvent(OrderDelivery deliveryUpdated) {
		OrderSagaEvent eventPayload = getSagaEvent(deliveryUpdated,CreateOrderSagaStep.STEP6_ORDER_DELIVERED,EventType.SAGA_CONTINUE);
		sendOrderSagaEvent(eventPayload);
	}
	
	private  void sendOrderSagaEvent(OrderSagaEvent eventPayload){
		EventProducerWrapper<OrderSagaEvent> eventWrapper = new EventProducerWrapper<>(DeliveryConstants.CREATE_ORDER_SAGA_TOPIC,
				eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);

	}

	@Async(DeliveryConstants.EVENT_TASK_EXECUTOR)
	public void createDeliveryRecordByOrder(OrderDeliveryEvent event) {
		try {
			OrderDelivery delivery = deliveryMapper.toEntity(event,ApiDeliveryStatusDto.WAIT_TO_SEND);
			deliveryRepository.save(delivery);
		} catch (Exception e) {
			log.error("[DeliveryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
			sendOrderSagaRollbackByException(event);
		}
		
	}
	
	private OrderSagaEvent getSagaEvent(OrderDelivery delivery,CreateOrderSagaStep step,EventType type) {
		Long date = null;
		String errorMessage = null;
		if(step.equals(CreateOrderSagaStep.STEP5_ORDER_SHIPPED) && type.equals(EventType.SAGA_CONTINUE)) {
			date = delivery.getShippingDate();
		}else if(step.equals(CreateOrderSagaStep.STEP5_ORDER_SHIPPED) && type.equals(EventType.SAGA_CONTINUE)) {
			date = delivery.getDeliveryDate();
		}else {
			errorMessage = "lost shipment";
		}	
		return OrderSagaEvent.builder()
				.errorMessages(errorMessage)
				.orderId(delivery.getIdOrder())
				.eventStepId(delivery.getDeliveryId())
				.eventStepDate(date)
				.stepInfo(step)
				.eventType(type)
				.build();
	}

	


	private void sendOrderSagaRollbackByException( OrderDeliveryEvent event) {
		OrderSagaEvent eventPayload = getSagaEventByException(event);
		sendOrderSagaEvent(eventPayload);
	}
	
	private OrderSagaEvent getSagaEventByException(OrderDeliveryEvent event) {
		return OrderSagaEvent.builder()
				.errorMessages("Delivery Service - Internal Server Error")
				.orderId(event.getOrderId())
				.eventStepId(null)
				.eventStepDate(null)
				.stepInfo(CreateOrderSagaStep.STEP5_ORDER_SHIPPED)
				.eventType(EventType.SAGA_ROLLBACK)
				.build();
	}
}
