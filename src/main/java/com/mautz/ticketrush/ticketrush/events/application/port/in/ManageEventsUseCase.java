package com.mautz.ticketrush.ticketrush.events.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.Event;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;

public interface ManageEventsUseCase {

    /** Creates the event and generates a rows x seatsPerRow seat map (labels A1, A2, ..., B1, ...). */
    Event create(Command command);

    List<Event> list();

    Event get(UUID eventId);

    /** @param status optional filter */
    List<Seat> seats(UUID eventId, Optional<SeatStatus> status);

    record Command(String name, String description, String venue, Instant startsAt, BigDecimal price, int rows,
            int seatsPerRow) {
    }
}
