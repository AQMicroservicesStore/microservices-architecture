package com.aqstore.service.warehouse.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
@Service
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

	public void sendOrderSagaEvent(OrderSagaEvent eventPayload) {
		EventProducerWrapper<OrderSagaEvent> eventWrapper = new EventProducerWrapper<>(WarehouseConstants.CREATE_ORDER_SAGA_TOPIC,
				eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}

	@Async(WarehouseConstants.EVENT_TASK_EXECUTOR)
	public void checkProductsQuantity(OrderCheckItemEvent payload) {
		try {
			OrderSagaEvent sagaEvent = defaultOrderSagaEvent(payload, EventType.SAGA_CONTINUE);
			Map<Long, EventOrderItemDTO> itemsMap = payload.getItems().stream()
					.collect(Collectors.toMap(EventOrderItemDTO::getItemId, order -> order));
			Set<Long> ids = itemsMap.keySet();
			List<Product> products = productRepository.findByIdIn(ids);
			if (products.size() != ids.size()) {
				sagaEvent.setErrorMessages("products not found");
				sagaEvent.setEventType(EventType.SAGA_ROLLBACK);
			}else {
				products.forEach(p -> {
					EventOrderItemDTO itemDto = itemsMap.get(p.getId());
					updateProductInfo(p, itemDto, sagaEvent);
					sagaEvent.getOrderItems().add(itemDto);
				});
				if (sagaEvent.getEventType().equals(EventType.SAGA_CONTINUE)) {
					products.forEach(p -> updateDbProduct(p));
				} else {
					sagaEvent.setErrorMessages("Products not available");
				}				
			}
			sendOrderSagaEvent(sagaEvent);
		} catch (Exception e) {
			log.error("[OrderCheckItems] : failed to consume event with Id=[{}]", payload.getEventId());
			AQStoreExceptionHandler.handleException(e);
			sendOrderSagaRollbackByException(payload);
		}

	}

	@Async(WarehouseConstants.EVENT_TASK_EXECUTOR)
	public void rollbackProductsQuantity(OrderCheckItemEvent payload) {
		try {

			OrderSagaEvent sagaEvent = defaultOrderSagaEvent(payload, EventType.IGNORE);
			Map<Long, EventOrderItemDTO> itemsMap = payload.getItems().stream()
					.collect(Collectors.toMap(EventOrderItemDTO::getItemId, order -> order));
			Set<Long> ids = itemsMap.keySet();
			List<Product> products = productRepository.findByIdIn(ids);
			products.forEach(p -> {
				EventOrderItemDTO itemDto = itemsMap.get(p.getId());
				Integer quantity = p.getStock().getQuantity();
				p.getStock().setQuantity(quantity + itemDto.getQuantity());
				updateDbProduct(p);
			});
			sendOrderSagaEvent(sagaEvent);
		} catch (Exception e) {
			log.error("[OrderCheckItems] : failed to consume event with Id=[{}]", payload.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}
	}
	
	
	

	/*
	 * private methods
	 */

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

	private OrderSagaEvent defaultOrderSagaEvent(OrderCheckItemEvent payload, EventType type) {
		return OrderSagaEvent.builder().eventType(type).stepInfo(CreateOrderSagaStep.STEP2_CHECK_ITEMS)
				.orderId(payload.getOrderId()).orderItems(new ArrayList<>()).errorMessages("").build();
	}
	
	private void sendOrderSagaRollbackByException( OrderCheckItemEvent event) {
 		OrderSagaEvent eventPayload = getSagaEventByException(event);
		sendOrderSagaEvent(eventPayload);
	}
	
	private OrderSagaEvent getSagaEventByException(OrderCheckItemEvent event) {
		return OrderSagaEvent.builder()
				.errorMessages("Warehouse - Internal Server Error")
				.orderId(event.getOrderId())
				.eventStepId(null)
				.eventStepDate(null)
				.stepInfo(CreateOrderSagaStep.STEP2_CHECK_ITEMS)
				.eventType(EventType.SAGA_ROLLBACK)
				.build();
	}

	
	
}
