package com.aqstore.service.catalog.event;

import com.aqstore.service.event.EventPayload;
import com.aqstore.service.event.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEvent implements EventPayload<Long>{

	
	private Long id;
	private Long createdDate;
	private int version;
	private String name;
	private String type;
	private String brand;
	private Integer weight;
	private String description;
	private Integer quantity;
	private Float priceToSell;
	private Float purchaseCost;
	private Long lastProductModifiedDate;	
	private Long lastStockModifiedDate;	
	private EventType eventType;
	
	@Override
	public Long getPayloadId() {
		return id;
	}

	
	
	
}
