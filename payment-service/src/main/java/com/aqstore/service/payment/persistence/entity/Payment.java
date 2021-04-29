package com.aqstore.service.payment.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Table("payment")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Payment extends Auditable{
	@Id
	private Long paymentId;
	private Long idOrder;
	private String transactionCode;
	private Long  transactionDate;
	private Float orderPrice;
	private String userId;
	private boolean  accepted;
	private boolean  refunded;
	private String  refundedDescription;
	private Long  refundedDate;


	
	

}
