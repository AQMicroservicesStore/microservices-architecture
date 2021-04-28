package com.aqstore.service.order.web;

import com.aqstore.service.order.OrderConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.OrderApi;
import com.aqstore.service.openapi.model.ApiOrderRequestDto;
import com.aqstore.service.openapi.model.ApiOrderResponseDto;
import com.aqstore.service.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(OrderConstants.ORDERS_V1_PREFIX)
@RequiredArgsConstructor
public class OrderController implements OrderApi{
	private final OrderService orderService;
	
	
	@Override
	public ResponseEntity<ApiOrderResponseDto> addOrder(ApiOrderRequestDto apiOrderRequestDto) {
		ApiOrderResponseDto response = orderService.addOrder(apiOrderRequestDto);
		return ResponseEntity.ok(response);
	}
	
	
	@Override
	public ResponseEntity<ApiOrderResponseDto> getOrderById(Long orderId) {
		ApiOrderResponseDto response = orderService.getOrderById(orderId);		
		return ResponseEntity.ok(response);
	}
	
	
}
