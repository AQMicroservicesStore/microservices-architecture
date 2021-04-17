package com.aqstore.service.catalog.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.aqstore.service.catalog.persistence.entity.CatalogProduct;

public interface SearchProductRepository extends ElasticsearchRepository<CatalogProduct, Long>{

	
	List<CatalogProduct> findByName(String name);
	
	Page<CatalogProduct> findByNameLikeOrBrandLikeOrType(String name,String brand,String type,Pageable pageable);

	
}
