package com.aqstore.service.delivery.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table("orderdelivery")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderDelivery extends Auditable{
	@Id
	private Long deliveryId;
	private Long idOrder;
	private String userId;
	private String deliveryAddress;
	private Long  shippingDate;
	private Long  deliveryDate;
	private String status;

	
}
