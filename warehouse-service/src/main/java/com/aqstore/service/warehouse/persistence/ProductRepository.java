package com.aqstore.service.warehouse.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aqstore.service.warehouse.persistence.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

	Optional<Product> findByName(String name);

	boolean existsByName(String name);

	
	
	
	
	


}
