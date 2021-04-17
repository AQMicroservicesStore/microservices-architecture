package com.aqstore.service.warehouse.mapper;

import java.time.Instant;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aqstore.service.openapi.model.ApiProductDto;
import com.aqstore.service.openapi.model.ApiProductTypeDto;
import com.aqstore.service.openapi.model.ApiStockDto;
import com.aqstore.service.warehouse.persistence.entity.Product;
import com.aqstore.service.warehouse.persistence.entity.Stock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductMapperImpl.class,StockMapperImpl.class,MapperConverter.class})
public class AbstractMapperTest {

	
	protected final Long PRODUCT_ID = 1L;
	protected final Float PRICE = 40.00F;
	protected final Float COST = 40.00F;
	protected final Instant DATE = Instant.now();
	protected final Integer QUANTITY = 10;
	protected final Integer WEIGHT = 10;
	protected final String PNAME = "pname";
	protected final String PDESCRIPTION = "pdesc";
	protected final String PBRAND = "pbrand";
	protected final ApiProductTypeDto TYPE = ApiProductTypeDto.DOMOTIC;

	
	protected ApiStockDto getAPIStockDto(boolean productIdPresent) {
		return new ApiStockDto()
		.productId(productIdPresent? PRODUCT_ID : null)
		.purchaseCost(COST)
		.priceToSell(PRICE)
		.quantity(QUANTITY);
	}
	
	protected Stock getStockEntity() {
		return Stock.builder()
				.productId(PRODUCT_ID)
				.createdDate(DATE.toEpochMilli())
				.lastModifiedDate(DATE.toEpochMilli())
				.priceToSell(PRICE)
				.purchaseCost(COST)
				.quantity(QUANTITY)
				.build();
	}
	
	protected ApiProductDto getApiProductDto(boolean productIdPresent,boolean stockPresent) {
		return new ApiProductDto()
		.id(productIdPresent? PRODUCT_ID : null)
		.productName(PNAME)
		.productType(TYPE)
		.productDescription(PDESCRIPTION)
		.brand(PBRAND)
		.weight(WEIGHT)
		.stock(stockPresent ? getAPIStockDto(false):null);
	}
	
	protected Product getProductEntity() {
		return Product.builder()
				.id(PRODUCT_ID)
				.name(PNAME)
				.type(TYPE.name())
				.description(PDESCRIPTION)
				.brand(PBRAND)
				.weight(WEIGHT)
				.createdDate(DATE.toEpochMilli())
				.lastModifiedDate(DATE.toEpochMilli())
				.build();
	}
	
	
	


}
