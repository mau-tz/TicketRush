package com.mautz.ticketrush.ticketrush.payment.application.service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.application.port.in.ManageReservationUseCase;
import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.payment.application.port.in.PayReservationUseCase;
import com.mautz.ticketrush.ticketrush.payment.application.port.out.PaymentGatewayPort;
import com.mautz.ticketrush.ticketrush.payment.application.port.out.PaymentRepositoryPort;
import com.mautz.ticketrush.ticketrush.payment.domain.Payment;
import com.mautz.ticketrush.ticketrush.payment.domain.PaymentStatus;
import com.mautz.ticketrush.ticketrush.shared.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PaymentService implements PayReservationUseCase {

    private final PaymentRepositoryPort payments;
    private final PaymentGatewayPort gateway;
    private final ManageReservationUseCase reservations;
    private final Clock clock;

    PaymentService(PaymentRepositoryPort payments, PaymentGatewayPort gateway, ManageReservationUseCase reservations,
            Clock clock) {
        this.payments = payments;
        this.gateway = gateway;
        this.reservations = reservations;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Payment pay(Command c) {
        Optional<Payment> previous = payments.findByIdempotencyKey(c.idempotencyKey());
        if (previous.isPresent()) {
            Payment p = previous.get();
            if (!p.reservationId().equals(c.reservationId()) || !p.userId().equals(c.userId())) {
                throw new ConflictException("Idempotency key was already used for a different request");
            }
            return p;
        }

        Reservation reservation = reservations.get(c.reservationId(), c.userId());
        boolean approved = gateway.charge(reservation.total(), c.cardToken());
        if (approved) {
            // takes the reservation lock and re-validates it; throws (rolling back) if expired or cancelled
            reservations.confirm(c.reservationId(), c.userId());
        }
        return payments.save(new Payment(UUID.randomUUID(), c.reservationId(), c.userId(), reservation.total(),
                approved ? PaymentStatus.APPROVED : PaymentStatus.DECLINED, c.idempotencyKey(), clock.instant()));
    }
}
