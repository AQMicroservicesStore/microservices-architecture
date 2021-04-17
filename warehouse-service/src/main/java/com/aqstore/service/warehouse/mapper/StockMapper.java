package com.aqstore.service.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.event.EventType;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.event.StockEvent;
import com.aqstore.service.warehouse.persistence.entity.Stock;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {

	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	Stock toEntity(ApiStockDto s);
	
	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(source = "s.quantity", target = "quantity")
	@Mapping(source = "s.priceToSell", target = "priceToSell")
	@Mapping(source = "s.purchaseCost", target = "purchaseCost")
	@Mapping(source = "productId", target = "productId")
	Stock toEntity(ApiStockDto s, Long productId);

	@Mapping(target = "lastModifiedDate", source = "lastModifiedDate",qualifiedByName = {"mapperConverter","longToInstant"})
	ApiStockDto toDTO(Stock s);

	
	void updateEntity(@MappingTarget Stock toUpdate, Stock request);


	@Mapping(target = "eventType", source = "eventType")
	@Mapping(target = ".", source = "stock")
	StockEvent toEvent(Stock stock,EventType eventType);






}
