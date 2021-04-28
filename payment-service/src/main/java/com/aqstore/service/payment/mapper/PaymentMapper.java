package com.aqstore.service.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.openapi.model.ApiPaymentDto;
import com.aqstore.service.payment.persistence.entity.Payment;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

	
	@Mapping(target = "paymentId", ignore = true)	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "confirmedDate", ignore = true)
	@Mapping(target = "refundedDate", ignore = true)
	@Mapping(target = "refunded", ignore = true)
	@Mapping(target = "accepted", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "transactionCode", expression = "java(java.util.UUID.randomUUID().toString())")
	@Mapping(target = "userId", ignore = true)
	@Mapping(target = "orderPrice",source = "price")	
	void updateEntity(@MappingTarget Payment paymentToUpdate, ApiPaymentDto apiPaymentDto);

	@Mapping(target = "confirmed",source = "accepted")	
	@Mapping(target = "refunded",source = "refunded")	
	@Mapping(target = "price",source = "orderPrice")
	@Mapping(target = "confirmedTs",source = "confirmedDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	@Mapping(target = "refundedTs",source = "refundedDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	ApiPaymentDto toDto(Payment paymentUpdated);

	
	@Mapping(target = "paymentId", ignore = true)	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "confirmedDate", ignore = true)
	@Mapping(target = "refundedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "accepted",ignore = true)	
	@Mapping(target = "transactionCode", ignore = true)
	@Mapping(target = "refunded", ignore = true)
	@Mapping(target = "idOrder",source="orderId")	
	Payment toEntity(OrderPaymentsEvent event);


}
