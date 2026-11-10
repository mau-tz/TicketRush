package com.mautz.ticketrush.ticketrush.payment.application.port.out;

import java.util.Optional;

import com.mautz.ticketrush.ticketrush.payment.domain.Payment;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> findByIdempotencyKey(String key);
}
