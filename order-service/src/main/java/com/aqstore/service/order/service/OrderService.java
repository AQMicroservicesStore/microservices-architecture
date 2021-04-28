package com.aqstore.service.order.service;

import com.aqstore.service.openapi.model.ApiOrderRequestDto;
import com.aqstore.service.openapi.model.ApiOrderResponseDto;

public interface OrderService {

	ApiOrderResponseDto addOrder(ApiOrderRequestDto apiOrderRequestDto);

	ApiOrderResponseDto getOrderById(Long orderId);

}
