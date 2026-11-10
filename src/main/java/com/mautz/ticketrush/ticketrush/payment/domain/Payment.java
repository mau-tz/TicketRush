package com.mautz.ticketrush.ticketrush.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Payment(UUID id, UUID reservationId, UUID userId, BigDecimal amount, PaymentStatus status,
        String idempotencyKey, Instant createdAt) {
}
