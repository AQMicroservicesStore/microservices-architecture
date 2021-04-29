package com.aqstore.service.order.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("purchase_item")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

	@Id
	private Long id;
	private Long itemId;
	private boolean inStock;
	private Integer quantity;
	
}
