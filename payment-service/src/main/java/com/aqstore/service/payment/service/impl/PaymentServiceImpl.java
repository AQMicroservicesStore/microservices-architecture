package com.aqstore.service.payment.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.exception.http.AQStoreBadRequestException;
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
	public ApiPaymentDto findByOrderId(Long orderId) {
		log.info("start to retrieve payment for order {}", orderId);
		try {
			Payment payment = paymentRepository.findByIdOrder(orderId).orElseThrow(
					() -> AQStoreExceptionHandler.handleException(PaymentExceptionType.PRODUCT_NOT_FOUND, orderId));
			ApiPaymentDto response = paymentMapper.toDto(payment);
			return response;
		} catch (Exception e) {
			log.error("failed to retrieve payment");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}

	@Override
	public ApiPaymentDto rejectPaymentOrder(Long orderId) {
		log.info("start to update payment for order {} to reject status", orderId);
		try {
			Payment paymentUpdated = null;
			Payment paymentToUpdate = paymentRepository.findByIdOrder(orderId)
					.orElseThrow(() -> AQStoreExceptionHandler.handleException(PaymentExceptionType.PRODUCT_NOT_FOUND,orderId)
					);
			
			if(!paymentToUpdate.isAccepted() && paymentToUpdate.getTransactionDate()==null) {
				paymentToUpdate.setTransactionCode(UUID.randomUUID().toString());
				paymentToUpdate.setTransactionDate(Instant.now().toEpochMilli());
				paymentUpdated=paymentRepository.save(paymentToUpdate);
				eventHandler.sendRejectPaymentSagaEvet(paymentUpdated);
			}else {
				paymentUpdated=paymentToUpdate;
			}
			ApiPaymentDto response = paymentMapper.toDto(paymentUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update payment to confirm status");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}


	@Override
	public ApiPaymentDto confirmPaymentOrder(Long orderId) {
		log.info("start to update payment for order {} to confirmed status", orderId);
		try {
			Payment paymentUpdated = null;
			Payment paymentToUpdate = paymentRepository.findByIdOrder(orderId)
					.orElseThrow(() -> AQStoreExceptionHandler.handleException(PaymentExceptionType.PRODUCT_NOT_FOUND,orderId)
					);
			if(!paymentToUpdate.isAccepted() && paymentToUpdate.getTransactionDate()==null) {
				paymentToUpdate.setAccepted(true);
				paymentToUpdate.setTransactionCode(UUID.randomUUID().toString());
				paymentToUpdate.setTransactionDate(Instant.now().toEpochMilli());
				paymentUpdated=paymentRepository.save(paymentToUpdate);
				eventHandler.sendConfirmPaymentSagaEvent(paymentUpdated);
			}else {
				paymentUpdated=paymentToUpdate;
			}
			ApiPaymentDto response = paymentMapper.toDto(paymentUpdated);
			return response;
		} catch (Exception e) {
			log.error("failed to update payment to confirm status");
			throw AQStoreExceptionHandler.handleException(e);
		}
	}
	
}
