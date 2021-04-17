package com.aqstore.service.catalog.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aqstore.service.catalog.CatalogExceptionType;
import com.aqstore.service.catalog.mapper.CatalogProductMapper;
import com.aqstore.service.catalog.persistence.SearchProductRepository;
import com.aqstore.service.catalog.persistence.entity.CatalogProduct;
import com.aqstore.service.catalog.service.SearchProductService;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiCatalogProductDto;
import com.aqstore.service.openapi.model.ApiCatalogProductsDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchProductServiceImpl implements SearchProductService{

    private final SearchProductRepository repository;
    private final CatalogProductMapper mapper;

	@Override
	public ApiCatalogProductDto showProductById(Long productId) {
		try {
			log.info("show product with id:{}",productId);
			CatalogProduct cp = repository.findById(productId).orElse(null);
			if(cp==null) {
				throw AQStoreExceptionHandler.handleException(CatalogExceptionType.PRODUCT_NOT_FOUND,productId);
			}
			return mapper.toDto(cp);
		}catch (Exception e) {
			log.error("failed to retrieve product with id {}",productId);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiCatalogProductsDto listproducts(Integer limit, Integer page, String filter) {
		try {
			Pageable pageable = PageRequest.of(page-1, limit);
			Page<CatalogProduct> productsPage = null;
			if(StringUtils.hasText(filter)) {
				productsPage =  repository.findByNameLikeOrBrandLikeOrType(filter, filter, filter,pageable);
			}else {
				productsPage = repository.findAll(pageable);
			}
			ApiCatalogProductsDto response  = mapper.toDto(productsPage,limit);
			return response;
		}catch (Exception e) {
			log.error("failed to retrieve products with params : {},{},{}",limit,page,filter);
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public void addProductToCatalog(CatalogProduct product) {
		try{
			log.info("add product with id:{}",product.getProductId());
			if(repository.existsById(product.getProductId())) {
				throw AQStoreExceptionHandler.handleException(CatalogExceptionType.PRODUCT_ALREADY_EXISTS,product.getProductId());
			}
			repository.save(product);
		}catch (Exception e) {
			log.error("failed to add product with id:{}",product.getProductId());
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public void deleteCatalogProduct(CatalogProduct product) {
		try{
			log.info("delete product with id:{}",product.getProductId());
			if(!repository.existsById(product.getProductId())) {
				throw AQStoreExceptionHandler.handleException(CatalogExceptionType.PRODUCT_NOT_FOUND,product.getProductId());
			}
			repository.deleteById(product.getProductId());
		}catch (Exception e) {
			log.error("failed to add product with id:{}",product.getProductId());
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public void updateCatalogProduct(CatalogProduct product) {
		try{
			log.info("update product with id:{}",product.getProductId());
			CatalogProduct productToUpdate = repository.findById(product.getProductId()).orElse(null);
			if(!repository.existsById(product.getProductId())) {
				throw AQStoreExceptionHandler.handleException(CatalogExceptionType.PRODUCT_NOT_FOUND,product.getProductId());
			}
			mapper.updateEntity(productToUpdate, product);
			repository.save(product);
		}catch (Exception e) {
			log.error("failed to add product with id:{}",product.getProductId());
			throw AQStoreExceptionHandler.handleException(e);
		}
	}


}
