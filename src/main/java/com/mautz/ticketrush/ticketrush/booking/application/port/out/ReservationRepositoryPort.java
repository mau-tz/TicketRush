package com.mautz.ticketrush.ticketrush.booking.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;

public interface ReservationRepositoryPort {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(UUID id);

    /** Loads the reservation taking a row lock, serializing concurrent confirm/cancel/expire. */
    Optional<Reservation> findByIdForUpdate(UUID id);

    List<Reservation> findOverduePending(Instant now);
}
