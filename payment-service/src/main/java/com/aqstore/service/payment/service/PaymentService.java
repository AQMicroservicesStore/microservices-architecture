package com.aqstore.service.payment.service;

import com.aqstore.service.openapi.model.ApiPaymentDto;

public interface PaymentService {

	ApiPaymentDto updatePaymentStatus(ApiPaymentDto apiPaymentDto);

	ApiPaymentDto findByOrderId(Long orderId);

}
