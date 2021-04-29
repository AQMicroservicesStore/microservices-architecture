package com.aqstore.service.payment.service;

import com.aqstore.service.openapi.model.ApiPaymentDto;

public interface PaymentService {


	ApiPaymentDto findByOrderId(Long orderId);

	ApiPaymentDto rejectPaymentOrder(Long orderId);

	ApiPaymentDto confirmPaymentOrder(Long orderId);

}
