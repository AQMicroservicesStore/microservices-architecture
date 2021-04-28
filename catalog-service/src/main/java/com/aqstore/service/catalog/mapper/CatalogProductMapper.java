package com.aqstore.service.catalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.aqstore.service.catalog.CatalogExceptionType;
import com.aqstore.service.catalog.persistence.entity.CatalogProduct;
import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiCatalogProductDto;
import com.aqstore.service.openapi.model.ApiCatalogProductsDto;


@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CatalogProductMapper  {

	@Mapping(target = "productId", source="id")
	@Mapping(target = "lastModifiedDate", source = "lastProductModifiedDate",  qualifiedByName = {"mapperConverter","longToLocalDateTime"})
	@Mapping(target = "createdDate", source = "createdDate",  qualifiedByName = {"mapperConverter","longToLocalDateTime"})
	@Mapping(target = "price", source = "priceToSell")
	@Mapping(target = "version", ignore = true)
	CatalogProduct toEntity(ProductEvent p);

	
	
	@Mapping(target = "availability", expression = "java(catalogProduct.getQuantity()>0)")
	@Mapping(target = "id", source="productId")
	@Mapping(target = "productType", source="type")
	@Mapping(target = "productName", source="name")
	ApiCatalogProductDto toDto(CatalogProduct catalogProduct);



	
	void updateEntity(@MappingTarget CatalogProduct toUpdate, CatalogProduct request);



	default ApiCatalogProductsDto toDto(Page<CatalogProduct> productsPage,Integer limit) {
		ApiCatalogProductsDto dto = new ApiCatalogProductsDto()
				.totalItems(0L)
				.itemsInCurrentPage(0)
				.itemsLimitPerPage(limit)
				.totalPage(0)
				.currentPage(0)
				.hasNextPage(false)
				.nextPage(null);
		if(productsPage!= null && productsPage.getTotalElements()>0) {
			if(productsPage.getNumber()>=productsPage.getTotalPages()){
				throw AQStoreExceptionHandler.handleException(CatalogExceptionType.PRODUCT_PAGE_BAD_REQUEST);
			}
			
			
			dto.setTotalItems(productsPage.getTotalElements());
			dto.setItemsInCurrentPage(productsPage.getNumberOfElements());
			dto.setItemsLimitPerPage(productsPage.getSize());
			dto.setTotalPage(productsPage.getTotalPages());
			dto.setCurrentPage(productsPage.getNumber()+1);
			if(!dto.getCurrentPage().equals(dto.getTotalPage())){
				dto.hasNextPage(true);
				dto.setNextPage(dto.getCurrentPage()+1);
			}
			productsPage.getContent().forEach(c-> {
				dto.addProductsItem(toDto(c));
			});
		}
		return dto;
	}

}
