package com.aqstore.service.warehouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aqstore.service.event.EventType;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.event.StockEvent;
import com.aqstore.service.warehouse.persistence.entity.Stock;


class StockMapperTest extends AbstractMapperTest{
	
	@Autowired 
	StockMapper mapper;


	@Test
	@DisplayName("Map to StockEntity By stockDTO")
	public void mapToStockEntityByDto(){
		ApiStockDto source = getAPIStockDto(true);
		Stock target = mapper.toEntity(source);
		assertEquals(source.getProductId(), target.getProductId(),"ProductId should be the same");
		assertEquals(source.getPriceToSell(), target.getPriceToSell(), "Price to sell should be the same");
		assertEquals(source.getPurchaseCost(), target.getPurchaseCost(),"Purchase Cost should be the same");
		assertEquals(source.getQuantity(), target.getQuantity(),"Quantity should be the same");
	}
	
	@Test
	@DisplayName("Map to StockEntity By stockDTO and productId")
	public void mapToStockEntityByDtoAndProductID(){
		ApiStockDto source = getAPIStockDto(false);
		Stock target = mapper.toEntity(source,PRODUCT_ID);
		assertEquals(PRODUCT_ID, target.getProductId(),"ProductId should be the same");
		assertEquals(source.getPriceToSell(), target.getPriceToSell(), "Price to sell should be the same");
		assertEquals(source.getPurchaseCost(), target.getPurchaseCost(),"Purchase Cost should be the same");
		assertEquals(source.getQuantity(), target.getQuantity(),"Quantity should be the same");
	}
	
	@Test
	@DisplayName("Map to dto By stockEntity")
	public void mapToDtoByStockEntity(){
		Stock source = getStockEntity();
		ApiStockDto target = mapper.toDTO(source);
		assertEquals(source.getProductId(), target.getProductId(),"ProductId should be the same");
		assertEquals(source.getPriceToSell(), target.getPriceToSell(), "Price to sell should be the same");
		assertEquals(source.getPurchaseCost(), target.getPurchaseCost(),"Purchase Cost should be the same");
		assertEquals(source.getQuantity(), target.getQuantity(),"Quantity should be the same");
		assertEquals(Instant.ofEpochMilli(source.getLastModifiedDate()), target.getLastModifiedDate(),"Last Modified Date should be the same");
	}
	
	

	@Test
	@DisplayName("update Stock With Mapper")
	public void updateStockWithMapper(){
		Stock target = getStockEntity();
		Stock source = Stock.builder()
					.priceToSell(null)
					.quantity(25)
					.productId(null)
					.build();
		mapper.updateEntity(target, source);
		assertEquals(PRODUCT_ID, target.getProductId(),"ProductId should be the same");
		assertEquals(PRICE, target.getPriceToSell(), "Price to sell should be the same");
		assertEquals(COST, target.getPurchaseCost(),"Purchase Cost should be the same");
		assertEquals(source.getQuantity(), target.getQuantity(),"Quantity should be the same");
		assertEquals(DATE.toEpochMilli(), target.getLastModifiedDate(),"Last Modified Date should be the same");
	}
	



	
	@Test
	@DisplayName("Map to eventDto By StockEntity")
	public void mapToEventDtoByStockEntity(){
		Stock source = getStockEntity();
		StockEvent target = mapper.toEvent(source, EventType.CREATED);
		assertEquals(EventType.CREATED, target.getEventType(),"EventType should be the same");
		assertEquals(source.getProductId(), target.getProductId(),"ProductId should be the same");
		assertEquals(source.getLastModifiedDate(), target.getLastModifiedDate(),"Last Modified Date should be the same");
		assertEquals(source.getPriceToSell(), target.getPriceToSell(), "Price to sell should be the same");
		assertEquals(source.getPurchaseCost(), target.getPurchaseCost(),"Purchase Cost should be the same");
		assertEquals(source.getQuantity(), target.getQuantity(),"Quantity should be the same");
	}
	
	
	

}