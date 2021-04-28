package com.aqstore.service.payment.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aqstore.service.payment.persistence.entity.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long>{

	Optional<Payment> findByIdOrder(Long idOrder);
}
