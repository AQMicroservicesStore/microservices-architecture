package com.aqstore.service.payment.persistence.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Auditable {

	@CreatedBy
	private String createdBy;

	@CreatedDate
	private Long createdDate;

	@LastModifiedBy
	private String lastModifiedBy;

	
	@Version
	private int version;

}
