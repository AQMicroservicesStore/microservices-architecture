package com.aqstore.service.delivery.service;

import com.aqstore.service.openapi.model.ApiDeliveryDto;

public interface DeliveryService {

	ApiDeliveryDto getDeliveryByOrderId(Long orderId);

	ApiDeliveryDto updateDeliveryToLostStatus(Long orderId);

	ApiDeliveryDto updateDeliveryToSendStatus(Long orderId);

	ApiDeliveryDto updateDeliveryToArrivedStatus(Long orderId);


}
