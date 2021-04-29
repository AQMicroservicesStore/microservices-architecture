package com.aqstore.service.delivery.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.delivery.DeliveryConstants;
import com.aqstore.service.delivery.service.DeliveryService;
import com.aqstore.service.openapi.DeliveryApi;
import com.aqstore.service.openapi.model.ApiDeliveryDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(DeliveryConstants.API_V1_PREFIX)
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {
	private final DeliveryService deliveryService;

	@Override
	public ResponseEntity<ApiDeliveryDto> getDeliveryOrderStatus(Long orderId) {
		ApiDeliveryDto response = deliveryService.getDeliveryByOrderId(orderId);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<ApiDeliveryDto> updateDeliveryToArrivedStatus(Long orderId) {
		ApiDeliveryDto response = deliveryService.updateDeliveryToArrivedStatus(orderId);
		return ResponseEntity.ok(response);
	}
	
	
	@Override
	public ResponseEntity<ApiDeliveryDto> updateDeliveryToSendStatus(Long orderId) {
		ApiDeliveryDto response = deliveryService.updateDeliveryToSendStatus(orderId);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<ApiDeliveryDto> updateDeliveryToLostStatus(Long orderId) {
		ApiDeliveryDto response = deliveryService.updateDeliveryToLostStatus(orderId);
		return ResponseEntity.ok(response);
	}
}
