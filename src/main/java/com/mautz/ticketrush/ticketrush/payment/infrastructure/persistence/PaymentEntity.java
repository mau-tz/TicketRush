package com.mautz.ticketrush.ticketrush.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.payment.domain.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
class PaymentEntity {

    @Id
    private UUID id;
    @Column(name = "reservation_id")
    private UUID reservationId;
    @Column(name = "user_id")
    private UUID userId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Column(name = "idempotency_key")
    private String idempotencyKey;
    @Column(name = "created_at")
    private Instant createdAt;
}
