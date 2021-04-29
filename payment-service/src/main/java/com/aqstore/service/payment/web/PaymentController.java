package com.aqstore.service.payment.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.openapi.PaymentApi;
import com.aqstore.service.openapi.model.ApiPaymentDto;
import com.aqstore.service.payment.PaymentConstants;
import com.aqstore.service.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping(PaymentConstants.PAYMENT_V1_PREFIX)
@RequiredArgsConstructor
public class PaymentController implements PaymentApi{
	private final PaymentService paymentService;
	
	
	
	@Override
	public ResponseEntity<ApiPaymentDto> confirmPaymentOrder(Long orderId) {
		ApiPaymentDto response = paymentService.confirmPaymentOrder(orderId);
		return ResponseEntity.ok(response);
	}
	
	
	@Override
	public ResponseEntity<ApiPaymentDto> rejectPaymentOrder(Long orderId) {
		ApiPaymentDto response = paymentService.rejectPaymentOrder(orderId);
		return ResponseEntity.ok(response);
	}
	

	@Override
	public ResponseEntity<ApiPaymentDto> getPayment(Long orderId) {
		ApiPaymentDto response = paymentService.findByOrderId(orderId);
		return ResponseEntity.ok(response);
	}
	
	
	
	

}
