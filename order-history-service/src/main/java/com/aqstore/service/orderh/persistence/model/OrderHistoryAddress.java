package com.aqstore.service.orderh.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryAddress {

	  private String city;
	  private String country;
	  private String cap;
	  private String street;

}
