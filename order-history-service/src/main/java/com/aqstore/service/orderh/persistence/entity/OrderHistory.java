package com.aqstore.service.orderh.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.aqstore.service.orderh.persistence.model.OrderHistoryAddress;
import com.aqstore.service.orderh.persistence.model.OrderHistoryItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Document(indexName = "orderhistoryindex")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistory {

	@Id 
    @Field(type = FieldType.Long,name = "orderId")
	private Long idOrder;
	
    @Field(name = "created-date", type = FieldType.Date, format = DateFormat.date_time)
	private LocalDateTime creationDate;
    @Field(name = "last-modified-date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime lastModifiedDate;	
    @Field(name = "payment-date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime paymentDate;
    @Field(name = "shipping-date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime shippingDate;	    
    @Field(name = "delivery-date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime deliveryDate;	    
    @Field(type = FieldType.Keyword,name = "idUser")
    private String idUser;   
    @Field(type = FieldType.Keyword,name = "status")
    private String status;   
    @Field(type = FieldType.Integer,name = "total-weight")
	private Integer totalWeight;
    @Field(type = FieldType.Text,name = "desc")
	private String description;
    @Field(type = FieldType.Float,name = "total-price")
	private Float totalPrice;
	
    @Field(type = FieldType.Object,name = "address")
    private OrderHistoryAddress address;
    
    @Field(type = FieldType.Object,name = "order-items")
    List<OrderHistoryItem> orderItems;
    
    	

	
	
	
}



