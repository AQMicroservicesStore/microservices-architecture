package com.aqstore.service.payment.service.impl;

import org.springframework.stereotype.Service;

import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiPaymentDto;
import com.aqstore.service.payment.PaymentExceptionType;
import com.aqstore.service.payment.event.PaymentEventHandler;
import com.aqstore.service.payment.mapper.PaymentMapper;
import com.aqstore.service.payment.persistence.PaymentRepository;
import com.aqstore.service.payment.persistence.entity.Payment;
import com.aqstore.service.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final PaymentRepository paymentRepository;
	private final PaymentMapper paymentMapper;
	private final PaymentEventHandler eventHandler;

	@Override
	public ApiPaymentDto updatePaymentStatus(ApiPaymentDto apiPaymentDto) {
		log.info("start to update payment for order {}", apiPaymentDto.getIdOrder());
		try {
			Payment paymentToUpdate = paymentRepository.findByIdOrder(apiPaymentDto.getIdOrder())
					.orElseThrow(() -> AQStoreExceptionHandler.handleException(PaymentExceptionType.PRODUCT_NOT_FOUND,
							apiPaymentDto.getIdOrder()));
			paymentMapper.updateEntity(paymentToUpdate, apiPaymentDto);
			Payment paymentUpdated = paymentRepository.save(paymentToUpdate);
			eventHandler.sendPaymentEvent(paymentUpdated);
			ApiPaymentDto response = paymentMapper.toDto(paymentUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update payment");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiPaymentDto findByOrderId(Long orderId) {
		log.info("start to update payment for order {}", orderId);
		try {
			Payment payment = paymentRepository.findByIdOrder(orderId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(PaymentExceptionType.PRODUCT_NOT_FOUND, orderId));
			ApiPaymentDto response = paymentMapper.toDto(payment);
			return response;
		} catch (Exception e) {
			log.error("failed to update payment");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

}
