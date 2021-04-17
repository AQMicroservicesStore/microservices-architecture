package com.aqstore.service.warehouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aqstore.service.event.EventType;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.event.ProductEvent;
import com.aqstore.service.warehouse.persistence.entity.Product;
import com.aqstore.service.warehouse.persistence.entity.Stock;

class ProductMapperTest extends AbstractMapperTest{
	
	@Autowired 
	ProductMapper mapper;

	
	@Test
	@DisplayName("Map to ProductEntity By ProductDTO")
	public void mapToProductEntityByDto(){
		ApiProductDto source = getApiProductDto(false,false);
		Product target = mapper.toEntity(source);
		assertEquals(source.getId(), target.getId(),"ProductId should be the same");
		assertEquals(source.getProductName(), target.getName(), "ProductName to sell should be the same");
		assertEquals(source.getProductDescription(), target.getDescription(),"Description should be the same");
		assertEquals(source.getProductType().name(), target.getType(),"Product Type should be the same");
		assertEquals(source.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");
	}
	
	@Test
	@DisplayName("Map to StockEntity By ProductDTO DefaultValues new Request")
	public void mapToStockEntityByDtoDefaultValues(){
		ApiProductDto source = getApiProductDto(false,false);
		Stock target = mapper.toStockEntityNewRequest(source,PRODUCT_ID);
		assertEquals(PRODUCT_ID, target.getProductId(),"ProductId should be the same");
		assertEquals(0.00F, target.getPriceToSell(), "PriceToSell to sell should be the same");
		assertEquals(0.00F, target.getPurchaseCost(), "PurchaseCost to sell should be the same");
		assertEquals(0,target.getQuantity(),"Quantity should be the same");
	}
	
	@Test
	@DisplayName("Map to StockEntity By ProductDTO new request no empty values")
	public void mapToStockEntityByDto(){
		ApiProductDto source = getApiProductDto(false,true);
		Stock target = mapper.toStockEntityNewRequest(source,PRODUCT_ID);
		assertEquals(PRODUCT_ID, target.getProductId(),"ProductId should be the same");
		assertEquals(source.getStock().getPriceToSell(), target.getPriceToSell(), "PriceToSell to sell should be the same");
		assertEquals(source.getStock().getPurchaseCost(), target.getPurchaseCost(), "PurchaseCost to sell should be the same");
		assertEquals(source.getStock().getQuantity(),target.getQuantity(),"Quantity should be the same");
	}
	
	@Test
	@DisplayName("Map to Dto By ProductEntity")
	public void mapToDtoByProductEntity(){
		Product source = getProductEntity();
		ApiProductDto target = mapper.toDTO(source);
		assertEquals(source.getId(), target.getId(),"ProductId should be the same");
		assertEquals(source.getName(), target.getProductName(), "ProductName to sell should be the same");
		assertEquals(source.getDescription(), target.getProductDescription(),"Description should be the same");
		assertEquals(source.getType(), target.getProductType().name(),"Product Type should be the same");
		assertEquals(source.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");
		assertNull(target.getStock(),"Stock should be null");
	}
	
	@Test
	@DisplayName("Map to Dto By ProductEntity and StockEntity")
	public void mapToDtoByProductEntityAndStockEntity(){
		Product source = getProductEntity();
		Stock source2 = getStockEntity();
		ApiProductDto target = mapper.toDTO(source,source2);
		assertEquals(source.getId(), target.getId(),"ProductId should be the same");
		assertEquals(source.getName(), target.getProductName(), "ProductName to sell should be the same");
		assertEquals(source.getDescription(), target.getProductDescription(),"Description should be the same");
		assertEquals(source.getType(), target.getProductType().name(),"Product Type should be the same");
		assertEquals(source.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");
		assertNotNull(target.getStock(),"Stock should be not null");
		assertEquals(source2.getProductId(), target.getStock().getProductId(),"Stock productId should be the same");
		assertEquals(source2.getQuantity(), target.getStock().getQuantity(),"Stock quantity should be the same");
		assertEquals(source2.getPriceToSell(), target.getStock().getPriceToSell(),"Stock priceToSell should be the same");
		assertEquals(source2.getPurchaseCost(), target.getStock().getPurchaseCost(),"Stock purchaseCost should be the same");
		assertEquals(Instant.ofEpochMilli(source2.getLastModifiedDate()), target.getStock().getLastModifiedDate(),"Stock lastModifiedDate should be the same");
	}

	@Test
	@DisplayName("Map to Event By ProductDTO")
	public void mapToEventByProductEntityAndStockEntity(){
		Product source = getProductEntity();
		Stock source2 = getStockEntity();
		ProductEvent target = mapper.toEvent(source,source2,EventType.CREATED);
		assertEquals(source.getId(), target.getId(),"ProductId should be the same");
		assertEquals(source.getCreatedDate(), target.getCreatedDate(),"CreatedDate should be the same");
		assertEquals(source.getVersion(), target.getVersion(),"Version should be the same");
		assertEquals(source.getName(), target.getName(), "ProductName to sell should be the same");
		assertEquals(source.getType(), target.getType(),"Product Type should be the same");
		assertEquals(source.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");	
		assertEquals(source.getDescription(), target.getDescription(),"Description should be the same");
		assertEquals(source.getLastModifiedDate(), target.getLastProductModifiedDate(),"LastProductModifiedDate should be the same");
		assertEquals(source2.getQuantity(), target.getQuantity(),"Quantity should be the same");
		assertEquals(source2.getPriceToSell(), target.getPriceToSell(),"priceToSell should be the same");
		assertEquals(source2.getPurchaseCost(), target.getPurchaseCost(),"priceToSell should be the same");
		assertEquals(source2.getLastModifiedDate(), target.getLastStockModifiedDate(),"LastStockModifiedDate should be the same");
		assertEquals(EventType.CREATED, target.getEventType(),"EventType should be the same");
	}


	
	@Test
	@DisplayName("Update Entity Test")
	public void updateEntityTest(){
		ApiProductDto sourceDto = new ApiProductDto()
				.productDescription("new description")
				.weight(35)
				.brand("");
		Product source = mapper.toEntity(sourceDto);
		Product targetTest = getProductEntity();
		Product target = getProductEntity();
		mapper.updateEntity(target, source);
		assertEquals(targetTest.getId(), target.getId(),"ProductId should be the same");
		assertEquals(targetTest.getName(), target.getName(), "ProductName to sell should be the same");
		assertEquals(source.getDescription(), target.getDescription(),"Description should be the same");
		assertEquals(targetTest.getType(), target.getType(),"Product Type should be the same");
		assertEquals(targetTest.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");
	}
	
	
	@Test
	@DisplayName("Update stock entity")
	public void updateStockEntityTest(){
		ApiStockDto source = new ApiStockDto().quantity(25);
		Stock targetTest = getStockEntity();
		Stock target = getStockEntity();

		mapper.updateStockEntity(target, source);
		assertEquals(targetTest.getProductId(), target.getProductId(),"ProductId should be the same");
		assertEquals(targetTest.getPriceToSell(), target.getPriceToSell(), "PriceToSell to sell should be the same");
		assertEquals(targetTest.getPurchaseCost(), target.getPurchaseCost(), "PurchaseCost to sell should be the same");
		assertEquals(source.getQuantity(),target.getQuantity(),"Quantity should be the same");
	}

	
	

}