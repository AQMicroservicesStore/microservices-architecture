package com.aqstore.service.orderh.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.orderh.OrderHistoryConstants;
import com.aqstore.service.orderh.mapper.OrderHistoryMapper;
import com.aqstore.service.orderh.persistence.entity.OrderHistory;
import com.aqstore.service.orderh.service.OrderHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderHistoryEventHandler {

	private final OrderHistoryService orderService;
	private final OrderHistoryMapper orderMapper;



	@Async(OrderHistoryConstants.EVENT_TASK_EXECUTOR)
	public void addNewOrder(OrderHistoryEvent event) {
		try {
			OrderHistory order = orderMapper.toNewEntity(event);
			orderService.addOrder(order);
		}catch (Exception e) {
			log.error("[OrderHistoryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}
		
	}

	@Async(OrderHistoryConstants.EVENT_TASK_EXECUTOR)
	public void updateOrder(OrderHistoryEvent event) {
		try {
			orderService.updateOrder(event);
		} catch (Exception e) {
			log.error("[OrderHistoryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
		}
		
	}
	
	
	
	
	
}
