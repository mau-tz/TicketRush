package com.mautz.ticketrush.ticketrush.booking.application.port.out;

import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.Ticket;

public interface TicketRepositoryPort {

    void saveAll(List<Ticket> tickets);

    List<Ticket> findByReservation(UUID reservationId);
}
