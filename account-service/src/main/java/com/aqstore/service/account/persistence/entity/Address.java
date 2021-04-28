package com.aqstore.service.account.persistence.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("user_address")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
	
	@Id
	private String userId;
	private String city;
	private String country;
	private String cap;
	private String street;
	private String fullName;

	@CreatedDate
	private Long createdDate;

	@LastModifiedDate
	private Long lastModifiedDate;
	
	@Version
	private int version;




}
