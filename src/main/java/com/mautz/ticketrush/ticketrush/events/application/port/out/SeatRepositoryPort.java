package com.mautz.ticketrush.ticketrush.events.application.port.out;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;

public interface SeatRepositoryPort {

    void saveAll(List<Seat> seats);

    List<Seat> findByEvent(UUID eventId, Optional<SeatStatus> status);

    List<Seat> findByReservation(UUID reservationId);

    /** Moves AVAILABLE seats of the event to HELD for the reservation; returns how many were actually held. */
    int hold(UUID eventId, Collection<UUID> seatIds, UUID reservationId);

    /** Moves HELD seats of the reservation back to AVAILABLE; returns how many were released. */
    int release(UUID reservationId);

    /** Moves HELD seats of the reservation to SOLD; returns how many were updated. */
    int markSold(UUID reservationId);
}
