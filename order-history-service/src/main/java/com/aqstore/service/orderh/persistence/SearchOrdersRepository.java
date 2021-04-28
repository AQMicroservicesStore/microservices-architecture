package com.aqstore.service.orderh.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.aqstore.service.orderh.persistence.entity.OrderHistory;

public interface SearchOrdersRepository extends ElasticsearchRepository<OrderHistory, Long>{

	
	Page<OrderHistory> findByIdUserAndStatus(String idUser, String status, Pageable pageable);

	Page<OrderHistory> findByIdUser(String idUser, Pageable pageable);

	Page<OrderHistory> findByStatus(String status, Pageable pageable);

	
}
