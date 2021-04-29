package com.aqstore.service.order.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.OrderCheckAddressEvent;
import com.aqstore.service.event.payload.OrderCheckItemEvent;
import com.aqstore.service.event.payload.OrderDeliveryEvent;
import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.event.payload.component.EventOrderItemDTO;
import com.aqstore.service.openapi.model.ApiOrderItemDto;
import com.aqstore.service.openapi.model.ApiOrderRequestDto;
import com.aqstore.service.openapi.model.ApiOrderResponseDto;
import com.aqstore.service.openapi.model.ApiOrderStatusDto;
import com.aqstore.service.order.persistence.entity.Order;
import com.aqstore.service.order.persistence.entity.OrderItem;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper  {


	@Mapping(target = "idUser", source = "createdBy")
	@Mapping(target = "lastUpdate", source = "lastModifiedDate",qualifiedByName = {"mapperConverter","longToInstant"})
	@Mapping(target = "items", source = "orderItems")
	ApiOrderResponseDto toDTO(Order p);



	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	@Mapping(target = "eventStepId",ignore = true)
	@Mapping(target = "eventStepDate",ignore = true)
	@Mapping(target = "userAddress", ignore = true)
	@Mapping(target = "stepInfo",ignore =  true)
	@Mapping(target = "orderId", source = "idOrder")
	@Mapping(target = "idUser", source = "createdBy")
	@Mapping(target = "creationDate", source = "createdDate")
	@Mapping(target = "lastUpdate", source = "lastModifiedDate")
	@Mapping(target = "totalPrice", source = "orderPrice")
	@Mapping(target = "description", source = "statusDescription")
	@Mapping(target = "orderItems", source = "orderItems",qualifiedByName = "eventOrderItemsByOrder")
	OrderHistoryEvent toOrderHistoryEvent(Order order);

	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	@Mapping(target = "orderId", source = "idOrder")
	@Mapping(target = "userId", source = "createdBy")
	OrderCheckAddressEvent toCheckAddressEvent(Order order);

	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	@Mapping(target = "orderId", source = "idOrder")
	@Mapping(target = "userId", source = "createdBy")
	@Mapping(target = "refundedDescription", source = "statusDescription")
	OrderPaymentsEvent toPaymentEvent(Order order);

	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	@Mapping(target = "orderId", source = "idOrder")
	@Mapping(target = "userId", source = "createdBy")
	@Mapping(target = "fullAddress", source = "deliveryAddress")
	OrderDeliveryEvent toDeliveryEvent(Order order);

	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	default Order toEntity(ApiOrderRequestDto p) {
		Order order =  new Order();
		order.setStatus(ApiOrderStatusDto.PENDING.name());
		order.setStatusDescription("check items availability");
		for (ApiOrderItemDto itemDto : p.getItems()) {
			OrderItem item = OrderItem.builder()
					.itemId(itemDto.getItemId())
					.quantity(itemDto.getQuantity())
					.inStock(false)
					.build();
			order.addOrderItem(item);
		}
		return order;
	}


	@Named("eventOrderItemsByOrder")
	default List<EventOrderItemDTO> eventOrderItemsByOrder(Set<OrderItem> orderItems){
		List<EventOrderItemDTO> eventOrderItems = new ArrayList<>();
		orderItems.forEach(o->{
			EventOrderItemDTO eventOrderItem = EventOrderItemDTO.builder()
					.itemId(o.getItemId())
					.quantity(o.getQuantity())
					.confirmed(o.isInStock())
					.price(null)
					.cost(null)
					.weight(null)
					.build();
			eventOrderItems.add(eventOrderItem);
		});
		return eventOrderItems;
	}



	default OrderCheckItemEvent toCheckItemEvent(Order order){
		return OrderCheckItemEvent.builder()
				.orderId(order.getIdOrder())
				.items(eventOrderItemsByOrder(order.getOrderItems()))
				.build();
	}


	default void updateOrderHistory(OrderHistoryEvent orderHistoryEvent, OrderSagaEvent sagaEvent){
		if(sagaEvent==null) {
			orderHistoryEvent.setStepInfo(CreateOrderSagaStep.STEP1_CREATE_ORDER);
			return;
		}
		orderHistoryEvent.setStepInfo(sagaEvent.getStepInfo());
		switch(sagaEvent.getStepInfo()){
			case STEP2_CHECK_ITEMS:
				orderHistoryEvent.setOrderItems(sagaEvent.getOrderItems());
				break;
			case STEP3_CHECK_ADDRESS:
				orderHistoryEvent.setUserAddress(sagaEvent.getUserAddress());
				break;
			case STEP4_CONFIRM_PAYMENT:
			case STEP5_ORDER_SHIPPED:
			case STEP6_ORDER_DELIVERED:
				orderHistoryEvent.setEventStepDate(sagaEvent.getEventStepDate());
				orderHistoryEvent.setEventStepId(sagaEvent.getEventStepId());
				break;
			default:
				break;

		}
	};






	private void updateOrderByCheckItems(Order orderToUpdate, OrderSagaEvent sagaEvent,boolean error){
		Map<Long,EventOrderItemDTO> map = sagaEvent.getOrderItems().stream().collect(Collectors.toMap(EventOrderItemDTO::getItemId,order-> order));
		orderToUpdate.setOrderCost(0.00F);
		orderToUpdate.setOrderPrice(0.00F);
		orderToUpdate.setTotalWeight(0);
		orderToUpdate.getOrderItems().forEach(o -> {
			EventOrderItemDTO eventItem = map.get(o.getItemId());
			boolean isInStock =  eventItem.isConfirmed();
			o.setInStock(isInStock);
			if(!error) {
				Float currentCost = orderToUpdate.getOrderCost();
				Float currentPrice = orderToUpdate.getOrderPrice();
				Integer currentWeight = orderToUpdate.getTotalWeight();
				currentCost+= eventItem.getCost()*eventItem.getQuantity();
				currentPrice+= eventItem.getPrice()*eventItem.getQuantity();
				currentWeight+= eventItem.getWeight()*eventItem.getQuantity();
				orderToUpdate.setOrderCost(currentCost);
				orderToUpdate.setOrderPrice(currentPrice);
				orderToUpdate.setTotalWeight(currentWeight);
			}			
		});
	}

	
	default void updateBySaga(Order orderToUpdate, OrderSagaEvent sagaEvent){
		switch(sagaEvent.getStepInfo()){
			case STEP1_CREATE_ORDER:
				break;
			case STEP2_CHECK_ITEMS:
				orderToUpdate.setStatus(ApiOrderStatusDto.WAITING_USER_ADDRESS.name());
				orderToUpdate.setStatusDescription("check user address");
				updateOrderByCheckItems(orderToUpdate,sagaEvent,false);
				break;
			case STEP3_CHECK_ADDRESS:
				orderToUpdate.setStatus(ApiOrderStatusDto.WAIT_PAYMENT.name());
				orderToUpdate.setStatusDescription("wait payment");
				orderToUpdate.setDeliveryAddress(sagaEvent.getUserAddress().getFullAddress());
				break;
			case STEP4_CONFIRM_PAYMENT:
				orderToUpdate.setStatus(ApiOrderStatusDto.WAIT_SHIPPING.name());
				orderToUpdate.setStatusDescription("wait shipping");
				orderToUpdate.setPaymentId(sagaEvent.getEventStepId());
				break;
			case STEP5_ORDER_SHIPPED:
				orderToUpdate.setStatus(ApiOrderStatusDto.WAIT_DELIVERY.name());
				orderToUpdate.setStatusDescription("wait delivery");
				orderToUpdate.setDeliveryId(sagaEvent.getEventStepId());
				break;
			case STEP6_ORDER_DELIVERED:
				orderToUpdate.setStatus(ApiOrderStatusDto.DELIVERED.name());
				orderToUpdate.setStatusDescription("Delivered, order closed");
				break;
		default:
			break;
		}
	}

	
	default void updateBySagaError(Order orderToAbort, OrderSagaEvent sagaEvent) {
		orderToAbort.setStatus(ApiOrderStatusDto.ERROR.name());
		orderToAbort.setStatusDescription(sagaEvent.getErrorMessages());
		switch(sagaEvent.getStepInfo()){
		case STEP2_CHECK_ITEMS:
			updateOrderByCheckItems(orderToAbort,sagaEvent,true);
			break;
		default:
			break;
		}
	}
	
}
