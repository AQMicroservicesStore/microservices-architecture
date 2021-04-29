package com.aqstore.service.delivery.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aqstore.service.delivery.persistence.entity.OrderDelivery;

public interface DeliveryRepository extends CrudRepository<OrderDelivery, Long>{
	
	Optional<OrderDelivery> findByIdOrder(Long idOrder);

	
}
