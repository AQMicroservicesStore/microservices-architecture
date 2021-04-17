package com.aqstore.service.catalog.service;

import com.aqstore.service.catalog.persistence.entity.CatalogProduct;
import com.aqstore.service.openapi.model.ApiCatalogProductDto;
import com.aqstore.service.openapi.model.ApiCatalogProductsDto;

public interface SearchProductService {

	ApiCatalogProductDto showProductById(Long productId);

	ApiCatalogProductsDto listproducts(Integer limit, Integer page, String filter);

	void addProductToCatalog(CatalogProduct product);

	void deleteCatalogProduct(CatalogProduct product);

	void updateCatalogProduct(CatalogProduct product);

		
}
	