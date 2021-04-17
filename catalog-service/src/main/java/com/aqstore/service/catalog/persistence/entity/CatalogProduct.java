package com.aqstore.service.catalog.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Document(indexName = "productindex")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogProduct {

	@Id 
    @Field(type = FieldType.Long,name = "productId")
	private Long productId;
	
    @Field(name = "created-date", type = FieldType.Date, format = DateFormat.date_time)
	private LocalDateTime createdDate;
    @Field(name = "last-modified-date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime lastModifiedDate;	
    @Field(type = FieldType.Text,name = "name")
	private String name;
    @Field(type = FieldType.Keyword,name = "type")
    private String type;
    @Field(type = FieldType.Keyword,name = "brand")
	private String brand;
    @Field(type = FieldType.Integer,name = "weight")
	private Integer weight;
    @Field(type = FieldType.Text,name = "desc")
	private String description;
    @Field(type = FieldType.Integer,name = "quantity")
	private Integer quantity;
    @Field(type = FieldType.Float,name = "price")
	private Float price;
	
	@Version
	private Long version;
	

}



