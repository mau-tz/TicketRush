package com.mautz.ticketrush.ticketrush.events.application.port.in;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.Seat;

/** Seat-level operations other modules (booking) rely on. Joins the caller's transaction. */
public interface SeatInventoryApi {

    /**
     * Atomically holds the seats for a reservation. Fails with a ConflictException, leaving nothing held,
     * if any seat is not available.
     */
    Hold hold(UUID eventId, Collection<UUID> seatIds, UUID reservationId);

    void release(UUID reservationId);

    void markSold(UUID reservationId);

    List<Seat> seatsOf(UUID reservationId);

    record Hold(BigDecimal unitPrice) {
    }
}
