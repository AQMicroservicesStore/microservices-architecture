package com.aqstore.service.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.openapi.model.ApiPaymentDto;
import com.aqstore.service.payment.persistence.entity.Payment;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

	

	@Mapping(target = "refunded",source = "refunded")	
	@Mapping(target = "price",source = "orderPrice")
	@Mapping(target = "transactionTs",source = "transactionDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	@Mapping(target = "refundedTs",source = "refundedDate",qualifiedByName = {"mapperConverter","longToInstant"})	
	ApiPaymentDto toDto(Payment paymentUpdated);

	

	default Payment toEntity(OrderPaymentsEvent event) {
		String refundedDescription = event.getEventType().equals(EventType.SAGA_ROLLBACK)? event.getRefundedDescription():null;
			return Payment.builder()
					.idOrder(event.getOrderId())
					.userId(event.getUserId())
					.refundedDescription(refundedDescription)
					.orderPrice(event.getOrderPrice())
					.build();
	}


}
