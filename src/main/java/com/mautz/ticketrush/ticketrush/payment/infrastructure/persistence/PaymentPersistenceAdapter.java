package com.mautz.ticketrush.ticketrush.payment.infrastructure.persistence;

import java.util.Optional;

import com.mautz.ticketrush.ticketrush.payment.application.port.out.PaymentRepositoryPort;
import com.mautz.ticketrush.ticketrush.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpa;

    PaymentPersistenceAdapter(PaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Payment save(Payment p) {
        PaymentEntity e = jpa.findById(p.id()).orElseGet(PaymentEntity::new);
        e.setId(p.id());
        e.setReservationId(p.reservationId());
        e.setUserId(p.userId());
        e.setAmount(p.amount());
        e.setStatus(p.status());
        e.setIdempotencyKey(p.idempotencyKey());
        e.setCreatedAt(p.createdAt());
        return toDomain(jpa.save(e));
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String key) {
        return jpa.findByIdempotencyKey(key).map(PaymentPersistenceAdapter::toDomain);
    }

    private static Payment toDomain(PaymentEntity e) {
        return new Payment(e.getId(), e.getReservationId(), e.getUserId(), e.getAmount(), e.getStatus(),
                e.getIdempotencyKey(), e.getCreatedAt());
    }
}
