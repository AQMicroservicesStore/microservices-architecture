package com.aqstore.service.order.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table("purchase_order")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends Auditable {
	

	

	
	@Id
	private Long idOrder;
	private String status;
	private String statusDescription;
	private String deliveryAddress;
	private Long paymentId;
	private Long deliveryId;
	private Float orderPrice;
	private Float orderCost;
	private Integer totalWeight;
	@MappedCollection(idColumn = "purchase_order")
	@Builder.Default private Set<OrderItem> orderItems = new HashSet<>();
	
   public void addOrderItem(OrderItem orderItem) {
	   this.orderItems.add(orderItem);
   }

}
