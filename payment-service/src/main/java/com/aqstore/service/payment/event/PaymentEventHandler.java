package com.aqstore.service.payment.event;

import java.time.Instant;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aqstore.service.event.EventProducerWrapper;
import com.aqstore.service.event.EventSupplier;
import com.aqstore.service.event.EventType;
import com.aqstore.service.event.payload.OrderPaymentsEvent;
import com.aqstore.service.event.payload.OrderSagaEvent;
import com.aqstore.service.event.payload.component.CreateOrderSagaStep;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.payment.PaymentConstants;
import com.aqstore.service.payment.mapper.PaymentMapper;
import com.aqstore.service.payment.persistence.PaymentRepository;
import com.aqstore.service.payment.persistence.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentEventHandler {
	private final PaymentRepository paymentRepository;
	private final PaymentMapper paymentMapper;
	private final EventSupplier eventSupplier;
	
	
	public void sendPaymentEvent(Payment payment) {
		EventType type = payment.isAccepted()? EventType.SAGA_CONTINUE  : EventType.SAGA_ROLLBACK;
		OrderSagaEvent sagaEvent = getSagaEvent(payment,type);
		EventProducerWrapper<OrderSagaEvent> eventWrapper = new EventProducerWrapper<>(PaymentConstants.SAGA_TOPIC,
				sagaEvent);
		eventSupplier.delegateToSupplier(eventWrapper);
	}
	
	@Bean
	public Consumer<OrderPaymentsEvent> paymentConsumer() throws Exception {
		return event -> {
			try {
				log.info("[PaymentConsumer] : Consuming event with Id=[{}] - createdAt=[{}] - payloadId[{}]", event.getEventId(),
						event.getEventCreationTimestamp().toString(),event.getPayloadId());
				switch (event.getEventType()) {
				
				case SAGA_CONTINUE:
					createPaymentRecordByOrder(event);
					break;
				case SAGA_ROLLBACK:
					refundPayment(event);
					break;
				default:
					break;
				}
				log.info("[PaymentConsumer] : event with Id=[{}] -  consumed with success", event.getEventId());
			} catch (Exception e) {
				log.error("[PaymentConsumer] : failed to consume event with Id=[{}]", event.getEventId());
				AQStoreExceptionHandler.handleException(e);
			}

		};

	}
	


	private void refundPayment(OrderPaymentsEvent event) {
		Payment payment = paymentRepository.findByIdOrder(event.getOrderId()).orElseThrow(
				()-> AQStoreExceptionHandler.handleException(null)
			);
		payment.setRefunded(true);
		payment.setRefundedDate(Instant.now().toEpochMilli());
		paymentRepository.save(payment);

	}

	private void createPaymentRecordByOrder(OrderPaymentsEvent event) {
		Payment payment = paymentMapper.toEntity(event);
		paymentRepository.save(payment);
		
	}

	private OrderSagaEvent getSagaEvent(Payment pay,EventType type) {
		return OrderSagaEvent.builder()
				.orderId(pay.getIdOrder())
				.eventStepId(pay.getPaymentId())
				.eventStepDate(pay.getConfirmedDate())
				.stepInfo(CreateOrderSagaStep.STEP4_CONFIRM_PAYMENT)
				.eventType(type)
				.build();
	}

}
