package com.aqstore.service.order.persistence;

import org.springframework.data.repository.CrudRepository;

import com.aqstore.service.order.persistence.entity.Order;

public interface OrderRepository extends CrudRepository<Order,Long>{

}
