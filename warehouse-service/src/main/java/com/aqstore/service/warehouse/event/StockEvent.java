package com.aqstore.service.warehouse.event;

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
public class StockEvent implements EventPayload<Long>{

	private Long productId;
	private Integer quantity;
	private Float priceToSell;
	private Float purchaseCost;
	private Long lastModifiedDate;	
	private EventType eventType;
	
	



	@Override
	public Long getPayloadId() {
		return productId;
	}
	
}
