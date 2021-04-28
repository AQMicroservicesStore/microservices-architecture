package com.aqstore.service.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.persistence.entity.Stock;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {

	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
//	@Mapping(target = "productId", source = "productId")
	//	@Mapping(target = "id", source = "stockId",qualifiedByName = {"mapperConverter","UUIDToString"})
	Stock toEntity(ApiStockDto s);
	

	@Mapping(target = "lastModifiedDate", source = "lastModifiedDate",qualifiedByName = {"mapperConverter","longToInstant"})
//	@Mapping(target = "stockId", source = "id",qualifiedByName = {"mapperConverter","stringToUUID"})
	ApiStockDto toDTO(Stock s);

	
	void updateEntity(@MappingTarget Stock toUpdate, Stock request);




}
