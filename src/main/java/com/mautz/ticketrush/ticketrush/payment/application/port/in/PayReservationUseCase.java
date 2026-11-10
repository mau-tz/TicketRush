package com.mautz.ticketrush.ticketrush.payment.application.port.in;

import java.util.UUID;

import com.mautz.ticketrush.ticketrush.payment.domain.Payment;

public interface PayReservationUseCase {

    /**
     * Pays and confirms a reservation. Repeating a call with the same idempotency key returns the original
     * result instead of charging twice.
     */
    Payment pay(Command command);

    record Command(UUID userId, UUID reservationId, String cardToken, String idempotencyKey) {
    }
}
