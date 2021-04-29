package com.aqstore.service.order.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.EventPayload;
import com.aqstore.service.event.payload.OrderCheckAddressEvent;
import com.aqstore.service.event.payload.OrderCheckItemEvent;
import com.aqstore.service.event.payload.OrderDeliveryEvent;
import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.order.OrderConstants;
import com.aqstore.service.order.OrderExceptionType;
import com.aqstore.service.order.mapper.OrderMapper;
import com.aqstore.service.order.persistence.OrderRepository;
import com.aqstore.service.order.persistence.entity.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestratorHandler {
	private final EventSupplier eventSupplier;
	private final OrderMapper mapper;
	private final OrderRepository orderRepository;


	//* 1) send event to check availability products and wait
	public void createOrderSaga(Order order) throws Exception {
		sendOrderHistoryEvent(order,null,EventType.CQRS_CREATED);
		sendOrderItemCheckEvent(order,EventType.SAGA_CONTINUE);
	}

	
	
	@Async(OrderConstants.EVENT_TASK_EXECUTOR)	
	public void continueSagaOrder(OrderSagaEvent sagaEvent) {
		try {
			Order orderToUpdate = orderRepository.findById(sagaEvent.getOrderId()).orElseThrow(
					()-> AQStoreExceptionHandler.handleException(OrderExceptionType.ORDER_NOT_FOUND,sagaEvent.getOrderId())
			);
			mapper.updateBySaga(orderToUpdate,sagaEvent);
			Order orderUpdated = orderRepository.save(orderToUpdate);
			sendOrderHistoryEvent(orderUpdated,sagaEvent,EventType.CQRS_UPDATED);
			switch (sagaEvent.getStepInfo()){
				case STEP2_CHECK_ITEMS:
					sendOrderUserAddressEvent(orderUpdated);
					break;
				case STEP3_CHECK_ADDRESS:
					sendPaymentEvent(orderUpdated,EventType.SAGA_CONTINUE);
					break;
				case STEP4_CONFIRM_PAYMENT:
					sendDeliveryEvent(orderUpdated,EventType.SAGA_CONTINUE);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			log.error("[OrderSagaConsumer] : failed to consume event with Id=[{}]", sagaEvent.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}
	}
	
	
	


	@Async(OrderConstants.EVENT_TASK_EXECUTOR)
	void handleRollbackError(OrderSagaEvent event) {
		log.error("Rollback error -> {}",event.getStepInfo().name());
	}

	@Async(OrderConstants.EVENT_TASK_EXECUTOR)
	void rollbackOrder(OrderSagaEvent sagaEvent) {
		try {
			Order orderToAbort = orderRepository.findById(sagaEvent.getOrderId()).orElseThrow(
					()-> AQStoreExceptionHandler.handleException(OrderExceptionType.ORDER_NOT_FOUND,sagaEvent.getOrderId())
			);
			mapper.updateBySagaError(orderToAbort,sagaEvent);
			Order orderAborted = orderRepository.save(orderToAbort);
			switch (sagaEvent.getStepInfo()){
				case STEP3_CHECK_ADDRESS:
				case STEP4_CONFIRM_PAYMENT:
					sendOrderItemCheckEvent(orderAborted,EventType.SAGA_ROLLBACK);
					break;
				case STEP5_ORDER_SHIPPED:
					sendOrderItemCheckEvent(orderAborted,EventType.SAGA_ROLLBACK);
					sendPaymentEvent(orderAborted,EventType.SAGA_ROLLBACK);
					break;
				case STEP6_ORDER_DELIVERED:
					sendPaymentEvent(orderAborted,EventType.SAGA_ROLLBACK);
					break;
				default:
					break;
			}
			sendOrderHistoryEvent(orderAborted,sagaEvent,EventType.CQRS_DELETED);
		} catch (Exception e) {
			log.error("[OrderSagaConsumer] : failed to consume event with Id=[{}]", sagaEvent.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}



	}






	//update orderHistory
	private void sendOrderHistoryEvent(Order order,OrderSagaEvent sagaEvent, EventType type) {
		OrderHistoryEvent eventPayload = mapper.toOrderHistoryEvent(order);
		mapper.updateOrderHistory(eventPayload,sagaEvent);
		eventPayload.setEventType(type);
		sendTo(OrderConstants.ORDER_HISTORY_INFO, eventPayload);
	}

	private void sendOrderItemCheckEvent(Order order,EventType type){
		OrderCheckItemEvent eventPayload = mapper.toCheckItemEvent(order);
		eventPayload.setEventType(type);
		sendTo(OrderConstants.ORDER_CHECK_ITEMS, eventPayload);
	}
	private void sendOrderUserAddressEvent(Order order){
		OrderCheckAddressEvent eventPayload = mapper.toCheckAddressEvent(order);
		eventPayload.setEventType(EventType.SAGA_CONTINUE);
		sendTo(OrderConstants.ORDER_CHECK_ADDRESS, eventPayload);
	}



	private void sendPaymentEvent(Order order,EventType type) {
		OrderPaymentsEvent eventPayload = mapper.toPaymentEvent(order);
		eventPayload.setEventType(type);
		sendTo(OrderConstants.ORDER_UNLOCK_PAYMENT, eventPayload);
	}

	private void sendDeliveryEvent(Order order,EventType type) {
		OrderDeliveryEvent eventPayload = mapper.toDeliveryEvent(order);
		eventPayload.setEventType(type);
		sendTo(OrderConstants.ORDER_UNLOCK_DELIVERY, eventPayload);
	}

	
	
	
	private void sendTo(String topic ,EventPayload payload) {
		EventProducerWrapper<?> eventWrapper = new EventProducerWrapper<>(topic, payload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}
	

	/*
	 * SAGA
	 * 2) send event to check userAddress and wait .   ----> orderCheckAddress - in - out
	 * 3) sendEvent to unlock payments . update orderHistory  ---->    orderUnlockPayments - in-out
	 * 4) wait paymentResponse . unlock deliveryService . updateOrderHistory
	 * 5) wait deliveryConfirmed . updateOrderHistory.
	 * 6) wait deliveryArrived . updateOrderHistory;
	 *  --> handle user address Missing
	 *  --> handle availability products
	 *  --> handle  payment refused
	 *  --> handle shipping lost -> refund payment
	 *
	 *  ----> orderCheckItemsRequest CREATE - ABORT
	 *  <---- orderCheckItemsResponse CONFIRMED ---> orderCheckAddressRequest ; refused
	 *
	 *
	 *  orderCheckAddressRequest
	 *  orderCheckAddressResponse
	 *  orderUnlockPaymentRequest
	 *
	 *
	 *
	 *
	 *
	 */


	// #  ->
	// source = #createorder - updateOrderHistory
	// processor = # orderCheckAddress -> orderUnlockPayment -> orderUnlockDeliveryService ->
	// consumer
	// consumer = deliveryConsumer



}
