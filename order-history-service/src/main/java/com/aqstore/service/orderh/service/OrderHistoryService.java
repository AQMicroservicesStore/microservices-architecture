package com.aqstore.service.orderh.service;

import com.aqstore.service.event.payload.OrderHistoryEvent;
import com.aqstore.service.openapi.model.ApiOrderDto;
import com.aqstore.service.openapi.model.ApiOrderStatusDto;
import com.aqstore.service.openapi.model.ApiOrdersDto;
import com.aqstore.service.orderh.persistence.entity.OrderHistory;

public interface OrderHistoryService {

	ApiOrdersDto listOrders(Integer limit, Integer page, String filter, ApiOrderStatusDto status);

	ApiOrderDto showOrderById(Long orderId);

	void addOrder(OrderHistory order);

	void updateOrder(OrderHistoryEvent orderHistory);

}
