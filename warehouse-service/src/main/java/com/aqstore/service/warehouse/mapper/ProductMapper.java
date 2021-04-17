package com.aqstore.service.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.EventType;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.event.ProductEvent;
import com.aqstore.service.warehouse.persistence.entity.Product;
import com.aqstore.service.warehouse.persistence.entity.Stock;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper  {

	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(source = "productName", target = "name" ,qualifiedByName = {"mapperConverter","checkEmptyString"})
	@Mapping(source = "productType", target = "type",qualifiedByName = {"mapperConverter","checkEmptyString"})
	@Mapping(source = "brand", target = "brand" ,qualifiedByName = {"mapperConverter","checkEmptyString"})
	@Mapping(source = "productDescription", target = "description",qualifiedByName = {"mapperConverter","checkEmptyString"})
	Product toEntity(ApiProductDto p);

	@Mapping(target = "productName", source = "name")
	@Mapping(target = "productType", source = "type")
	@Mapping(target = "productDescription", source = "description")
	@Mapping(target = "stock", ignore = true)
	ApiProductDto toDTO(Product p);

	@Mapping(target = "productName", source = "p.name")
	@Mapping(target = "productType", source = "p.type")
	@Mapping(target = "productDescription", source = "p.description")
	@Mapping(target = "stock.quantity", source = "s.quantity")
	@Mapping(target = "stock.priceToSell", source = "s.priceToSell")
	@Mapping(target = "stock.purchaseCost", source = "s.purchaseCost")
	@Mapping(target = "stock.productId", source = "s.productId")
	@Mapping(target = "stock.lastModifiedDate", source = "s.lastModifiedDate",qualifiedByName = {"mapperConverter","longToInstant"})
	ApiProductDto toDTO(Product p, Stock s);

	
	
	@Mapping(target = "name", ignore = true)
	void updateEntity(@MappingTarget Product toUpdate, Product request);

	
	
	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	void updateStockEntity (@MappingTarget Stock target,ApiStockDto source);

	default Stock toStockEntityNewRequest(ApiProductDto dto,Long productId) {
		Stock stock = Stock.builder()
				.productId(productId)
				.priceToSell(0.00F)
				.purchaseCost(0.00F)
				.quantity(0)
				.build();
		updateStockEntity(stock, dto.getStock());
		return stock;
	}


	@Mapping(target = "id", source = "p.id")
	@Mapping(target = "createdDate", source = "p.createdDate")
	@Mapping(target = "version", source = "p.version")
	@Mapping(target = "name", source = "p.name")
	@Mapping(target = "type", source = "p.type")
	@Mapping(target = "brand", source = "p.brand")
	@Mapping(target = "weight", source = "p.weight")
	@Mapping(target = "description", source = "p.description")
	@Mapping(target = "lastProductModifiedDate", source = "p.lastModifiedDate")
	@Mapping(target = "quantity", source = "s.quantity")
	@Mapping(target = "priceToSell", source = "s.priceToSell")
	@Mapping(target = "purchaseCost", source = "s.purchaseCost")
	@Mapping(target = "lastStockModifiedDate", source = "s.lastModifiedDate")
	@Mapping(target = "eventType", source = "eventType")
	ProductEvent toEvent(Product p, Stock s, EventType eventType);

}
