package com.aqstore.service.order.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiOrderRequestDto;
import com.aqstore.service.openapi.model.ApiOrderResponseDto;
import com.aqstore.service.order.OrderExceptionType;
import com.aqstore.service.order.event.OrderSagaOrchestrator;
import com.aqstore.service.order.mapper.OrderMapper;
import com.aqstore.service.order.persistence.OrderRepository;
import com.aqstore.service.order.persistence.entity.Order;
import com.aqstore.service.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
	private final OrderRepository orderRepository;
	private final OrderMapper mapper;
	private final OrderSagaOrchestrator sagaOrchestrator;
	
	@Override
	@Transactional
	public ApiOrderResponseDto addOrder(ApiOrderRequestDto apiOrderRequestDto) {
		JwtAuthenticationToken authentication=(JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		log.info("try to create new order by User: {}",authentication.getName());
		try {
			Order orderToSave = mapper.toEntity(apiOrderRequestDto);
			Order order = orderRepository.save(orderToSave);
			sagaOrchestrator.createOrderSaga(order);
			ApiOrderResponseDto response =  mapper.toDTO(order);
			return response;
		}catch(Exception e) {
			log.error("failed to create order ",e);
			throw AQStoreExceptionHandler.handleException(e);
	
		}
	}

	@Override
	public ApiOrderResponseDto getOrderById(Long orderId) {
		JwtAuthenticationToken authentication=(JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		log.info("try to retrieve order id: {} -  by User :  {}",authentication.getName());
		try {
			Order order = retrieveOrder(orderId);
			ApiOrderResponseDto response =  mapper.toDTO(order);
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve order with id {}",orderId);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}
	
	
	private Order retrieveOrder(Long orderId) {
		Order order = orderRepository.findById(orderId).orElse(null);
		if (order == null) {
			throw AQStoreExceptionHandler.handleException(OrderExceptionType.ORDER_NOT_FOUND, orderId);
		}
		return order;
	}	
}
