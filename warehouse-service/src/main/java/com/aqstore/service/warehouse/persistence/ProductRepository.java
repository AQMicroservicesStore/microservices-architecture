package com.aqstore.service.warehouse.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.aqstore.service.warehouse.persistence.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

	Optional<Product> findByName(String name);
	
	List<Product> findByIdIn(Set<Long> ids);
	
//	
//	@Query("select * from Product p where p.stock.id = :stockId")
//	Optional<Product> findByStockId(@Param("email") Long stockId);

	boolean existsByName(String name);

	
	
	
	
	


}
