package com.aqstore.service.warehouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aqstore.service.event.payload.ProductEvent;
import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiStockDto;
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
		assertEquals(source.getWeight(), target.getWeight(),"Weight should be the same");
		assertEquals(0, target.getStock().getQuantity(),"quantity should be the same");
		assertEquals(0.00F, target.getStock().getPriceToSell(),"quantity should be the same");
		assertEquals(0.00F, target.getStock().getPurchaseCost(),"quantity should be the same");

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
		assertEquals(source.getStock().getQuantity(),target.getStock().getQuantity(),"Quantity must be the same");
		assertEquals(source.getStock().getPriceToSell(),target.getStock().getPriceToSell(),"getPriceToSell must be the same");
		assertEquals(source.getStock().getPurchaseCost(),target.getStock().getPurchaseCost(),"getPurchaseCost must be the same");

	}

	@Test
	@DisplayName("Map to Event By ProductDTO")
	public void mapToEventByProductEntityAndStockEntity(){
		Product source = getProductEntity();
		ProductEvent target = mapper.toEvent(source);
		assertEquals(source.getId(), target.getId(),"ProductId should be the same");
		assertEquals(source.getCreatedDate(), target.getCreatedDate(),"CreatedDate should be the same");
		assertEquals(source.getVersion(), target.getVersion(),"Version should be the same");
		assertEquals(source.getName(), target.getName(), "ProductName to sell should be the same");
		assertEquals(source.getType(), target.getType(),"Product Type should be the same");
		assertEquals(source.getBrand(), target.getBrand(),"Brand should be the same");
		assertEquals(source.getWeight(), target.getWeight(),"Weigh should be the same");	
		assertEquals(source.getDescription(), target.getDescription(),"Description should be the same");
		assertEquals(source.getLastModifiedDate(), target.getLastProductModifiedDate(),"LastProductModifiedDate should be the same");
		assertEquals(source.getStock().getQuantity(), target.getQuantity(),"Quantity should be the same");
		assertEquals(source.getStock().getPriceToSell(), target.getPriceToSell(),"priceToSell should be the same");
		assertEquals(source.getStock().getPurchaseCost(), target.getPurchaseCost(),"priceToSell should be the same");
		assertEquals(source.getStock().getLastModifiedDate(), target.getLastStockModifiedDate(),"LastStockModifiedDate should be the same");
	}
	
//	
	@Test
	@DisplayName("Update stock entity")
	public void updateStockEntityTest(){
		ApiStockDto source = new ApiStockDto().quantity(25);
		Stock targetTest = getStockEntity();
		Stock target = getStockEntity();

		mapper.updateStockEntity(target, source);
//		assertEquals(targetTest.getProductId(), target.getProductId(),"ProductId should be the same");
		assertEquals(targetTest.getPriceToSell(), target.getPriceToSell(), "PriceToSell to sell should be the same");
		assertEquals(targetTest.getPurchaseCost(), target.getPurchaseCost(), "PurchaseCost to sell should be the same");
		assertEquals(source.getQuantity(),target.getQuantity(),"Quantity should be the same");
	}
//
//	
	

}