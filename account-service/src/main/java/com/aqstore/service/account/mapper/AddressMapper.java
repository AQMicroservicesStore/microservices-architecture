package com.aqstore.service.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import com.aqstore.service.account.persistence.entity.Address;
import com.aqstore.service.event.payload.component.EventUserAddressDTO;
import com.aqstore.service.openapi.model.ApiAddressDto;

@Component
@Mapper(componentModel = "spring",uses = MapperConverter.class , nullValueCheckStrategy =NullValueCheckStrategy.ALWAYS ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper  {

	
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(source = "userId", target = "userId")
	@Mapping(source = "fullName", target = "fullName")
	@Mapping(source = "a.city", target = "city",qualifiedByName = {"mapperConverter","checkEmptyString"})
	@Mapping(source = "a.country", target = "country" ,qualifiedByName = {"mapperConverter","checkEmptyString"})
	@Mapping(source = "a.street", target = "street",qualifiedByName = {"mapperConverter","checkEmptyString"})
	Address toEntity(ApiAddressDto a,String userId,String fullName);
//

	ApiAddressDto toDTO(Address a);
	
	
	EventUserAddressDTO toEventDto(Address userAddress);

	
//	
//	@Mapping(target = "name", ignore = true)
//	void updateEntity(@MappingTarget Product toUpdate, Product request);
//
//
//	@Mapping(target = "id", source = "p.id")
//	@Mapping(target = "createdDate", source = "p.createdDate")
//	@Mapping(target = "version", source = "p.version")
//	@Mapping(target = "name", source = "p.name")
//	@Mapping(target = "type", source = "p.type")
//	@Mapping(target = "brand", source = "p.brand")
//	@Mapping(target = "weight", source = "p.weight")
//	@Mapping(target = "description", source = "p.description")
//	@Mapping(target = "lastProductModifiedDate", source = "p.lastModifiedDate")
//	@Mapping(target = "quantity", source = "s.quantity")
//	@Mapping(target = "priceToSell", source = "s.priceToSell")
//	@Mapping(target = "purchaseCost", source = "s.purchaseCost")
//	@Mapping(target = "lastStockModifiedDate", source = "s.lastModifiedDate")
//	@Mapping(target = "eventType", source = "eventType")
//	ProductEvent toEvent(Product p, Stock s, EventType eventType);

}
