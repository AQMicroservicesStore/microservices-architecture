package com.aqstore.service.orderh.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryItem {

	
	  private Long id;
	  private String name;
	  private Integer quantity;
	  private Float price;
	
	
}
