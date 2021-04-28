package com.aqstore.service.orderh.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.OrdersApi;
import com.aqstore.service.openapi.model.ApiOrderDto;
import com.aqstore.service.openapi.model.ApiOrderStatusDto;
import com.aqstore.service.openapi.model.ApiOrdersDto;
import com.aqstore.service.orderh.OrderHistoryConstants;
import com.aqstore.service.orderh.service.OrderHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(OrderHistoryConstants.API_V1_PREFIX)
@RequiredArgsConstructor
public class OrderHistoryController implements OrdersApi{
	private final OrderHistoryService ordersService;
	
	@Override
	public ResponseEntity<ApiOrdersDto> listOrders(Integer limit, Integer page, String filter,
			ApiOrderStatusDto status) {
		ApiOrdersDto response = ordersService.listOrders(limit,page,filter,status);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<ApiOrderDto> showOrderById(Long orderId) {
		ApiOrderDto response = ordersService.showOrderById(orderId);
		return ResponseEntity.ok(response);
	}
}
