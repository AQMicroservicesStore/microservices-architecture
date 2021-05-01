package com.aqstore.service.payment.event;

import java.time.Instant;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.exception.AbstractAQStoreException;
import com.aqstore.service.payment.PaymentConstants;
import com.aqstore.service.payment.mapper.PaymentMapper;
import com.aqstore.service.payment.persistence.PaymentRepository;
import com.aqstore.service.payment.persistence.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventHandler {
	private final PaymentRepository paymentRepository;
	private final PaymentMapper paymentMapper;
	private final EventSupplier eventSupplier;
	
	
	public void sendConfirmPaymentSagaEvent(Payment payment) {
		OrderSagaEvent eventPayload = getSagaEvent(payment,EventType.SAGA_CONTINUE);
		sendOrderSagaEvent(eventPayload);
	}
	
	
	public void sendRejectPaymentSagaEvet(Payment payment) {
		OrderSagaEvent eventPayload = getSagaEvent(payment,EventType.SAGA_ROLLBACK);
		sendOrderSagaEvent(eventPayload);
	}
	


	@Async(PaymentConstants.EVENT_TASK_EXECUTOR)
	void refundPayment(OrderPaymentsEvent event) {
		try {
			Payment payment = paymentRepository.findByIdOrder(event.getOrderId()).orElseThrow(
					()-> AQStoreExceptionHandler.handleException(null)
				);
			payment.setRefunded(true);
			payment.setRefundedDate(Instant.now().toEpochMilli());
			payment.setRefundedDescription(event.getRefundedDescription());
			paymentRepository.save(payment);
		} catch (AbstractAQStoreException e) {
			log.error("[PaymentConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
			sendOrderSagaRollbackByException(event);
		}
	}

	@Async(PaymentConstants.EVENT_TASK_EXECUTOR)
	void createPaymentRecordByOrder(OrderPaymentsEvent event) {
		try {
			Payment payment = paymentMapper.toEntity(event);
			paymentRepository.save(payment);
		} catch (Exception e) {
			log.error("[PaymentConsumer] : failed to consume event with Id=[{}]", event.getEventId());
			AQStoreExceptionHandler.handleException(e);
			sendOrderSagaRollbackByException(event);
		}
		
	}

	private void sendOrderSagaEvent(OrderSagaEvent eventPayload) {
		EventProducerWrapper<OrderSagaEvent> eventWrapper = new EventProducerWrapper<>(PaymentConstants.CREATE_ORDER_SAGA_TOPIC,
				eventPayload);
		eventSupplier.delegateToSupplier(eventWrapper);
	}
	
	private OrderSagaEvent getSagaEvent(Payment pay,EventType type) {
		return OrderSagaEvent.builder()
				.errorMessages(type.equals(EventType.SAGA_ROLLBACK)? "payment refused" : null)
				.orderId(pay.getIdOrder())
				.eventStepId(pay.getPaymentId())
				.eventStepDate(pay.getTransactionDate())
				.stepInfo(CreateOrderSagaStep.STEP4_CONFIRM_PAYMENT)
				.eventType(type)
				.build();
	}

	private void sendOrderSagaRollbackByException( OrderPaymentsEvent event) {
		OrderSagaEvent eventPayload = getSagaEventByException(event);
		sendOrderSagaEvent(eventPayload);
	}
	
	private OrderSagaEvent getSagaEventByException(OrderPaymentsEvent event) {
		return OrderSagaEvent.builder()
				.errorMessages("Payment Service - Internal Server Error")
				.orderId(event.getOrderId())
				.eventStepId(null)
				.eventStepDate(null)
				.stepInfo(CreateOrderSagaStep.STEP4_CONFIRM_PAYMENT)
				.eventType(EventType.SAGA_ROLLBACK)
				.build();
	}

	
	
	
	
	
	
}
