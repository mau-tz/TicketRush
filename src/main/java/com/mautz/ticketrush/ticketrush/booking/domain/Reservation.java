package com.mautz.ticketrush.ticketrush.booking.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.shared.exception.BusinessRuleException;

public class Reservation {

    private final UUID id;
    private final UUID userId;
    private final UUID eventId;
    private final List<UUID> seatIds;
    private final BigDecimal total;
    private final Instant expiresAt;
    private final Instant createdAt;
    private ReservationStatus status;

    public Reservation(UUID id, UUID userId, UUID eventId, List<UUID> seatIds, BigDecimal total,
            ReservationStatus status, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.seatIds = List.copyOf(seatIds);
        this.total = total;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static Reservation create(UUID id, UUID userId, UUID eventId, List<UUID> seatIds, BigDecimal unitPrice,
            Duration holdTime, Instant now) {
        return new Reservation(id, userId, eventId, seatIds, unitPrice.multiply(BigDecimal.valueOf(seatIds.size())),
                ReservationStatus.PENDING, now.plus(holdTime), now);
    }

    public boolean isOverdue(Instant now) {
        return status == ReservationStatus.PENDING && !expiresAt.isAfter(now);
    }

    public void confirm(Instant now) {
        requirePending("confirm");
        if (!expiresAt.isAfter(now)) {
            throw new BusinessRuleException("Reservation hold has expired");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        requirePending("cancel");
        status = ReservationStatus.CANCELLED;
    }

    public void expire() {
        requirePending("expire");
        status = ReservationStatus.EXPIRED;
    }

    private void requirePending(String action) {
        if (status != ReservationStatus.PENDING) {
            throw new BusinessRuleException("Cannot " + action + " a reservation that is " + status);
        }
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public UUID eventId() { return eventId; }
    public List<UUID> seatIds() { return seatIds; }
    public BigDecimal total() { return total; }
    public ReservationStatus status() { return status; }
    public Instant expiresAt() { return expiresAt; }
    public Instant createdAt() { return createdAt; }
}
