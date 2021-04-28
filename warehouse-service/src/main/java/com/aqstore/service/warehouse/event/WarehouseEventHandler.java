package com.aqstore.service.warehouse.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderCheckItemEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.event.payload.component.EventOrderItemDTO;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.warehouse.WarehouseConstants;
import com.aqstore.service.warehouse.mapper.ProductMapper;
import com.aqstore.service.warehouse.persistence.ProductRepository;
import com.aqstore.service.warehouse.persistence.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WarehouseEventHandler {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final EventSupplier eventSupplier;

	public void sendProductEvent(Product product, EventType type) {
		ProductEvent eventPayload = productMapper.toEvent(product);
		eventPayload.setEventType(type);
		EventProducerWrapper<ProductEvent> eventWrapper = new EventProducerWrapper<>(WarehouseConstants.PRODUCT_TOPIC,
				eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}

	@Bean
	public Function<OrderCheckItemEvent, OrderSagaEvent> orderCheckItems() {
		return event -> {
			try {
				log.info("[OrderCheckItems] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());			
				switch (event.getEventType()) {
					case SAGA_ROLLBACK:
						return rollbackProductsQuantity(event);
					case SAGA_CONTINUE:
						return checkProductsQuantity(event);
					default:
						return defaultPayload(event,EventType.IGNORE);
				}
			}catch (Exception e) {
				log.error("[OrderCheckItems] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
				return defaultPayload(event, EventType.SAGA_ERROR_ROLLBACK);
			}

		};
	}
	

	private OrderSagaEvent defaultPayload(OrderCheckItemEvent payload,EventType type) {
		return OrderSagaEvent.builder()
			.eventType(type)
			.stepInfo(CreateOrderSagaStep.STEP2_CHECK_ITEMS)
			.orderId(payload.getOrderId())
			.orderItems(new ArrayList<>())
			.errorMessages("")
			.build();
	}
	
	

	private OrderSagaEvent checkProductsQuantity(OrderCheckItemEvent payload) {		
		OrderSagaEvent sagaEvent = defaultPayload(payload, EventType.SAGA_CONTINUE);
		Map<Long, EventOrderItemDTO> itemsMap = payload.getItems().stream().collect(Collectors.toMap(EventOrderItemDTO::getItemId, order -> order));
		Set<Long> ids = itemsMap.keySet();
		List<Product> products = productRepository.findByIdIn(ids);
		if(products.size()!= ids.size()) {
			sagaEvent.setErrorMessages("products not found");
			sagaEvent.setEventType(EventType.SAGA_ROLLBACK);
			return sagaEvent;
		}	
		products.forEach(p -> {
			EventOrderItemDTO itemDto = itemsMap.get(p.getId());
			updateProductInfo(p, itemDto, sagaEvent);
			sagaEvent.getOrderItems().add(itemDto);
		});
		if(sagaEvent.getEventType().equals(EventType.SAGA_CONTINUE)) {
			products.forEach(p-> updateDbProduct(p));		
		}else {
			sagaEvent.setErrorMessages("Products not available");
		}
		return sagaEvent;
	}

	
	

	private OrderSagaEvent rollbackProductsQuantity(OrderCheckItemEvent payload) {
		OrderSagaEvent sagaEvent = defaultPayload(payload, EventType.IGNORE);
		Map<Long, EventOrderItemDTO> itemsMap = payload.getItems().stream().collect(Collectors.toMap(EventOrderItemDTO::getItemId, order -> order));
		Set<Long> ids = itemsMap.keySet();
		List<Product> products = productRepository.findByIdIn(ids);
		products.forEach(p-> {
			EventOrderItemDTO itemDto = itemsMap.get(p.getId());
			Integer quantity = p.getStock().getQuantity();
			p.getStock().setQuantity(quantity + itemDto.getQuantity());
			updateDbProduct(p);
		});
		return sagaEvent;
	}


	private void updateProductInfo(Product p, EventOrderItemDTO itemDto, OrderSagaEvent sagaEvent) {
		Integer quantity = p.getStock().getQuantity();
		itemDto.setPrice(p.getStock().getPriceToSell());
		itemDto.setCost(p.getStock().getPurchaseCost());
		itemDto.setName(p.getName());
		itemDto.setWeight(p.getWeight());
		if (itemDto.getQuantity() <= quantity) {
			itemDto.setConfirmed(true);
			p.getStock().setQuantity(quantity - itemDto.getQuantity());
		} else {
			itemDto.setConfirmed(false);
			sagaEvent.setEventType(EventType.SAGA_ROLLBACK);
		}

	}

	
	
	private void updateDbProduct(Product p) {
		Product pUpdated = productRepository.save(p);
		sendProductEvent(pUpdated, EventType.CQRS_UPDATED);
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