package com.aqstore.service.orderh.event;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.orderh.mapper.OrderHistoryMapper;
import com.aqstore.service.orderh.persistence.entity.OrderHistory;
import com.aqstore.service.orderh.service.OrderHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderHistoryEventHandler {

	private final OrderHistoryService orderService;
	private final OrderHistoryMapper orderMapper;

	
	@Bean
	public Consumer<OrderHistoryEvent> orderHistoryConsumer() throws Exception {
		return event -> {
			try {
				log.info("[OrderHistoryConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				case CQRS_CREATED:
					OrderHistory order = orderMapper.toNewEntity(event);
					orderService.addOrder(order);
					break;
				case CQRS_UPDATED:
					orderService.updateOrder(event);
					break;
				default:
					break;
				}
				log.info("[OrderHistoryConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[OrderHistoryConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
	
	
	
	
	
}
