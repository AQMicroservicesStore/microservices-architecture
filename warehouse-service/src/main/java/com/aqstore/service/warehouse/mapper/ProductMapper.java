package com.aqstore.service.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiStockDto;
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
	@Mapping(target = "stock",expression = "java(this.createStock(p.getStock()))")
	Product toEntity(ApiProductDto p);

	@Mapping(target = "productName", source = "name")
	@Mapping(target = "productType", source = "type")
	@Mapping(target = "productDescription", source = "description")
	@Mapping(target = "stock.quantity", source = "stock.quantity")
	@Mapping(target = "stock.priceToSell", source = "stock.priceToSell")
	@Mapping(target = "stock.purchaseCost", source = "stock.purchaseCost")
//	@Mapping(target = "stock.productId", source = "stock.productId")
//	@Mapping(target = "stock.stockId", source = "stock.id",qualifiedByName = {"mapperConverter","stringToUUID"})
	@Mapping(target = "stock.lastModifiedDate", source = "stock.lastModifiedDate",qualifiedByName = {"mapperConverter","longToInstant"})
	ApiProductDto toDTO(Product p);

		
	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
//	@Mapping(target = "id", ignore = true)
	void updateStockEntity (@MappingTarget Stock target,ApiStockDto source);

	
	
	@Named("createStock")
	default Stock createStock(ApiStockDto dto) {
		Stock stock = Stock.builder()
//				.id(UUID.randomUUID().toString())
				.priceToSell(0.00F)
				.purchaseCost(0.00F)
				.quantity(0)
				.build();
		updateStockEntity(stock, dto);
		return stock;
	}


	@Mapping(target = "eventType",ignore = true)
	@Mapping(target = "eventId",ignore = true)
	@Mapping(target = "eventCreationTimestamp",ignore = true)
	@Mapping(target = "id", source = "p.id")
	@Mapping(target = "createdDate", source = "p.createdDate")
	@Mapping(target = "version", source = "p.version")
	@Mapping(target = "name", source = "p.name")
	@Mapping(target = "type", source = "p.type")
	@Mapping(target = "brand", source = "p.brand")
	@Mapping(target = "weight", source = "p.weight")
	@Mapping(target = "description", source = "p.description")
	@Mapping(target = "lastProductModifiedDate", source = "p.lastModifiedDate")
	@Mapping(target = "quantity", source = "p.stock.quantity")
	@Mapping(target = "priceToSell", source = "p.stock.priceToSell")
	@Mapping(target = "purchaseCost", source = "p.stock.purchaseCost")
	@Mapping(target = "lastStockModifiedDate", source = "p.stock.lastModifiedDate")
	ProductEvent toEvent(Product p);

}
