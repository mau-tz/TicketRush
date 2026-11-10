package com.mautz.ticketrush.ticketrush.booking.application.port.in;

import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.booking.domain.Ticket;

/** All operations are scoped to the owner: another user's reservation looks like it does not exist. */
public interface ManageReservationUseCase {

    Reservation get(UUID reservationId, UUID userId);

    Reservation cancel(UUID reservationId, UUID userId);

    /** Confirms a paid reservation: seats become SOLD and tickets are issued. Used by the payment module. */
    Reservation confirm(UUID reservationId, UUID userId);

    List<Ticket> tickets(UUID reservationId, UUID userId);
}
