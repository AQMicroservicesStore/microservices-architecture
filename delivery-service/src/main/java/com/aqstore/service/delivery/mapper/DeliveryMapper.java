package com.aqstore.service.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.delivery.persistence.entity.OrderDelivery;
import com.aqstore.service.event.payload.OrderDeliveryEvent;
import com.aqstore.service.openapi.model.ApiDeliveryDto;
import com.aqstore.service.openapi.model.ApiDeliveryStatusDto;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryMapper {
	
	

	@Mapping(target = "deliveryId", ignore = true)	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "shippingDate", ignore = true)
	@Mapping(target = "deliveryDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "userId", ignore = true)
	void updateEntity(@MappingTarget OrderDelivery deliveryToUpdate, ApiDeliveryDto dto);

	
	
	@Mapping(target = "deliveryDate",source = "deliveryDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	@Mapping(target = "shippingDate",source = "shippingDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	ApiDeliveryDto toDto(OrderDelivery deliveryUpdated);

	

	@Mapping(target = "deliveryId", ignore = true)	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "shippingDate", ignore = true)
	@Mapping(target = "deliveryDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "deliveryAddress", source = "event.fullAddress")
	@Mapping(target = "idOrder", source = "event.orderId")
	@Mapping(target = "status", source = "status")
	OrderDelivery toEntity(OrderDeliveryEvent event,ApiDeliveryStatusDto status);

	
	
	
	
	
	
	
	
	

}
