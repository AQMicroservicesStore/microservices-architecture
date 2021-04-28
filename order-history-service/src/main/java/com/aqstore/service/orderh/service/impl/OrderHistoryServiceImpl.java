package com.aqstore.service.orderh.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiOrderDto;
import com.aqstore.service.openapi.model.ApiOrderStatusDto;
import com.aqstore.service.openapi.model.ApiOrdersDto;
import com.aqstore.service.orderh.OrderHistoryExceptionType;
import com.aqstore.service.orderh.mapper.OrderHistoryMapper;
import com.aqstore.service.orderh.persistence.SearchOrdersRepository;
import com.aqstore.service.orderh.persistence.entity.OrderHistory;
import com.aqstore.service.orderh.service.OrderHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

	private final SearchOrdersRepository repository;
	private final OrderHistoryMapper mapper;
	
	
	
	@Override
	public ApiOrdersDto listOrders(Integer limit, Integer page, String filter, ApiOrderStatusDto status) {
		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		if(isAdmin(authentication)) {
			return  listAdminOrders(limit,page,status);
		}else {
			return listCustomerOrders(limit,page,status,authentication.getName());
		}
	}

	private ApiOrdersDto listCustomerOrders(Integer limit, Integer page, ApiOrderStatusDto status,String username) {
		try {
			Pageable pageable = PageRequest.of(page-1, limit);
			Page<OrderHistory> productsPage = null;
			if(status!=null) {
				productsPage =  repository.findByIdUserAndStatus(username,status.name(),pageable);
			}else {
				productsPage = repository.findByIdUser(username,pageable);
			}
			ApiOrdersDto response  = mapper.toDto(productsPage,limit);
			return response;
		}catch (Exception e) {
			log.error("failed to retrieve orders with params : {},{},{}",limit,page,status);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	private ApiOrdersDto listAdminOrders(Integer limit, Integer page, ApiOrderStatusDto status) {
		try {
			Pageable pageable = PageRequest.of(page-1, limit);
			Page<OrderHistory> productsPage = null;
			if(status!=null) {
				productsPage =  repository.findByStatus(status.name(),pageable);
			}else {
				productsPage = repository.findAll(pageable);
			}
			ApiOrdersDto response  = mapper.toDto(productsPage,limit);
			return response;
		}catch (Exception e) {
			log.error("failed to retrieve orders with params : {},{},{}",limit,page,status);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiOrderDto showOrderById(Long orderId) {
		try {
			log.info("show order with id:{}",orderId);
			OrderHistory cp = repository.findById(orderId).orElseThrow(
					()-> AQStoreExceptionHandler.handleException(OrderHistoryExceptionType.ORDER_NOT_FOUND,orderId)
			);

			return mapper.toDto(cp);
		}catch (Exception e) {
			log.error("failed to retrieve order with id {}",orderId);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

		

	private boolean isAdmin(JwtAuthenticationToken authentication) {
		for (GrantedAuthority s : authentication.getAuthorities()) {
			String authority = s.getAuthority().toLowerCase();
			if (authority.contains("administrator")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addOrder(OrderHistory order) {
		try{
			log.info("add order with id:{}",order.getIdOrder());
			if(repository.existsById(order.getIdOrder())) {
				throw AQStoreExceptionHandler.handleException(OrderHistoryExceptionType.ORDER_ALREADY_EXISTS,order.getIdOrder());
			}
			repository.save(order);
		}catch (Exception e) {
			log.error("failed to add order with id:{}",order.getIdOrder());
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public void updateOrder(OrderHistoryEvent event) {
		try{
			log.info("update order with id:{}",event.getOrderId());			
			OrderHistory orderToUpdate = repository.findById(event.getOrderId()).orElseThrow(
					()->AQStoreExceptionHandler.handleException(OrderHistoryExceptionType.ORDER_NOT_FOUND,event.getOrderId())
			);			
			mapper.updateEntity(orderToUpdate, event);
			repository.save(orderToUpdate);
		}catch (Exception e) {
			log.error("failed to update order with id:{}",event.getOrderId());
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

}
