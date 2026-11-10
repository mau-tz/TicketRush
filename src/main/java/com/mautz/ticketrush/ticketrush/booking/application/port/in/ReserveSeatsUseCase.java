package com.mautz.ticketrush.ticketrush.booking.application.port.in;

import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;

public interface ReserveSeatsUseCase {

    Reservation reserve(Command command);

    record Command(UUID userId, UUID eventId, List<UUID> seatIds) {
    }
}
