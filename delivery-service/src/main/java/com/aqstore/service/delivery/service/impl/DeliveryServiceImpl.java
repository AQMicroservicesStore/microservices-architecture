package com.aqstore.service.delivery.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.aqstore.service.delivery.DeliveryExceptionType;
import com.aqstore.service.delivery.event.DeliveryEventHandler;
import com.aqstore.service.delivery.mapper.DeliveryMapper;
import com.aqstore.service.delivery.persistence.DeliveryRepository;
import com.aqstore.service.delivery.persistence.entity.OrderDelivery;
import com.aqstore.service.delivery.service.DeliveryService;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiDeliveryDto;
import com.aqstore.service.openapi.model.ApiDeliveryStatusDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService{
	private final DeliveryRepository deliveryRepository;
	private final DeliveryMapper deliveryMapper;
	private final DeliveryEventHandler eventHandler;
	
	
	
	@Override
	public ApiDeliveryDto getDeliveryByOrderId(Long orderId) {
		log.info("start to retrieve delivery for order {}",orderId);
		try {
			OrderDelivery delivery = deliveryRepository.findByIdOrder(orderId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(DeliveryExceptionType.DELIVERY_NOT_FOUND, orderId));
			ApiDeliveryDto response = deliveryMapper.toDto(delivery);
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve delivery");
			throw AQStoreExceptionHandler.handleException(e);

		}
	}


	@Override
	public ApiDeliveryDto updateDeliveryToLostStatus(Long orderId) {
		log.info("start to update delivery for order {} to Lost status",orderId);
		try {
			OrderDelivery deliveryUpdated = updateDeliveryStatus(orderId, ApiDeliveryStatusDto.LOST);
			eventHandler.sendDeliveryLostSagaEvent(deliveryUpdated);
			ApiDeliveryDto response = deliveryMapper.toDto(deliveryUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update delivery");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiDeliveryDto updateDeliveryToSendStatus(Long orderId) {
		log.info("start to update delivery for order {} to Send status",orderId);
		try {
			OrderDelivery deliveryUpdated = updateDeliveryStatus(orderId, ApiDeliveryStatusDto.SEND);
			eventHandler.sendDeliveryShippedSagaEvent(deliveryUpdated);
			ApiDeliveryDto response = deliveryMapper.toDto(deliveryUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update delivery");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiDeliveryDto updateDeliveryToArrivedStatus(Long orderId) {
		log.info("start to update delivery for order {} to Arrived status",orderId);
		try {
			OrderDelivery deliveryUpdated = updateDeliveryStatus(orderId, ApiDeliveryStatusDto.ARRIVED);
			eventHandler.sendDeliveryArrivedSagaEvent(deliveryUpdated);
			ApiDeliveryDto response = deliveryMapper.toDto(deliveryUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update delivery");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	
	
	
	
	private OrderDelivery updateDeliveryStatus(Long orderId, ApiDeliveryStatusDto status) {
			OrderDelivery delivery = deliveryRepository.findByIdOrder(orderId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(DeliveryExceptionType.DELIVERY_NOT_FOUND, orderId));
			delivery.setStatus(status.name());
			if(status.equals(ApiDeliveryStatusDto.SEND)) {
				delivery.setShippingDate(Instant.now().toEpochMilli());
			}else if(status.equals(ApiDeliveryStatusDto.ARRIVED)) {
				delivery.setDeliveryDate(Instant.now().toEpochMilli());
			}
			OrderDelivery deliveryUpdated = deliveryRepository.save(delivery);
			return deliveryUpdated;
	}
}
