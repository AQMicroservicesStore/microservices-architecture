package com.aqstore.service.orderh.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.event.payload.component.EventOrderItemDTO;
import com.aqstore.service.event.payload.component.EventUserAddressDTO;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiOrderDto;
import com.aqstore.service.openapi.model.ApiOrdersDto;
import com.aqstore.service.orderh.OrderHistoryExceptionType;
import com.aqstore.service.orderh.persistence.entity.OrderHistory;
import com.aqstore.service.orderh.persistence.model.OrderHistoryAddress;
import com.aqstore.service.orderh.persistence.model.OrderHistoryItem;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderHistoryMapper {

	
	@Mapping(target = "creationDate", source = "creationDate",  qualifiedByName = {"mapperConverter","ldtToInstant"})
	@Mapping(target = "lastUpdate", source = "lastModifiedDate",  qualifiedByName = {"mapperConverter","ldtToInstant"})
	@Mapping(target = "paymentDate", source = "paymentDate",  qualifiedByName = {"mapperConverter","ldtToInstant"})
	@Mapping(target = "shippingDate", source = "shippingDate",  qualifiedByName = {"mapperConverter","ldtToInstant"})
	@Mapping(target = "deliveryDate", source = "deliveryDate",  qualifiedByName = {"mapperConverter","ldtToInstant"})
	@Mapping(target = "elements", source = "orderItems")
	@Mapping(target = "deliveryAddress", source = "address")	
	ApiOrderDto toDto(OrderHistory p);

    List<OrderHistoryItem> toHistoryItemList(List<EventOrderItemDTO> eventItemsList);
   
	@Mapping(target = "id", source = "itemId")	
    OrderHistoryItem toHistoryItem(EventOrderItemDTO eventItemDto);
	
    OrderHistoryAddress toAddressDto(EventUserAddressDTO address);
    	

	default ApiOrdersDto toDto(Page<OrderHistory> ordersPage, Integer limit) {
		ApiOrdersDto dto = new ApiOrdersDto()
				.totalItems(0L)
				.itemsInCurrentPage(0)
				.itemsLimitPerPage(limit)
				.totalPage(0)
				.currentPage(0)
				.hasNextPage(false)
				.nextPage(null);
		if(ordersPage!= null && ordersPage.getTotalElements()>0) {
			if(ordersPage.getNumber()>=ordersPage.getTotalPages()){
				throw AQStoreExceptionHandler.handleException(OrderHistoryExceptionType.ORDER_PAGE_BAD_REQUEST);
			}
			
			
			dto.setTotalItems(ordersPage.getTotalElements());
			dto.setItemsInCurrentPage(ordersPage.getNumberOfElements());
			dto.setItemsLimitPerPage(ordersPage.getSize());
			dto.setTotalPage(ordersPage.getTotalPages());
			dto.setCurrentPage(ordersPage.getNumber()+1);
			if(!dto.getCurrentPage().equals(dto.getTotalPage())){
				dto.hasNextPage(true);
				dto.setNextPage(dto.getCurrentPage()+1);
			}
			ordersPage.getContent().forEach(c-> {
				dto.addProductsItem(toDto(c));
			});
		}
		return dto;
	}
	

	
	default OrderHistory toNewEntity(OrderHistoryEvent event) {
		return OrderHistory.builder()
				.creationDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getCreationDate()),ZoneId.systemDefault()))
				.idUser(event.getIdUser())
				.idOrder(event.getOrderId())
				.status(event.getStatus())
				.description(event.getDescription())
				.lastModifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getLastUpdate()),ZoneId.systemDefault()))
				.build();
	}
	

	default void updateEntity(OrderHistory orderToUpdate, OrderHistoryEvent event) {
		orderToUpdate.setStatus(event.getStatus());
		orderToUpdate.setDescription(event.getDescription());
		orderToUpdate.setLastModifiedDate(
				LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getLastUpdate()), ZoneId.systemDefault()));

		switch (event.getStepInfo()) {
		case STEP2_CHECK_ITEMS:
			orderToUpdate.setTotalPrice(event.getTotalPrice());
			orderToUpdate.setTotalWeight(event.getTotalWeight());
			orderToUpdate.setOrderItems(toHistoryItemList(event.getOrderItems()));
			break;
		case STEP3_CHECK_ADDRESS:
			orderToUpdate.setAddress(toAddressDto(event.getUserAddress()));
			break;
		case STEP4_CONFIRM_PAYMENT:
			orderToUpdate.setPaymentDate(
					LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEventStepDate()), ZoneId.systemDefault()));
			break;
		case STEP5_ORDER_SHIPPED:
			orderToUpdate.setShippingDate(
					LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEventStepDate()), ZoneId.systemDefault()));
			break;
		case STEP6_ORDER_DELIVERED:
			orderToUpdate.setDeliveryDate(
					LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEventStepDate()), ZoneId.systemDefault()));
			break;
		default:
			break;
		}
	}
	
	
}
