package com.mautz.ticketrush.ticketrush.events.application.service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.application.port.in.ManageEventsUseCase;
import com.mautz.ticketrush.ticketrush.events.application.port.in.SeatInventoryApi;
import com.mautz.ticketrush.ticketrush.events.application.port.out.EventRepositoryPort;
import com.mautz.ticketrush.ticketrush.events.application.port.out.SeatRepositoryPort;
import com.mautz.ticketrush.ticketrush.events.domain.Event;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import com.mautz.ticketrush.ticketrush.shared.exception.BusinessRuleException;
import com.mautz.ticketrush.ticketrush.shared.exception.ConflictException;
import com.mautz.ticketrush.ticketrush.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class EventService implements ManageEventsUseCase, SeatInventoryApi {

    private static final int MAX_ROWS = 26;
    private static final int MAX_SEATS_PER_ROW = 100;

    private final EventRepositoryPort events;
    private final SeatRepositoryPort seats;
    private final Clock clock;

    EventService(EventRepositoryPort events, SeatRepositoryPort seats, Clock clock) {
        this.events = events;
        this.seats = seats;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Event create(ManageEventsUseCase.Command c) {
        if (c.rows() < 1 || c.rows() > MAX_ROWS || c.seatsPerRow() < 1 || c.seatsPerRow() > MAX_SEATS_PER_ROW) {
            throw new BusinessRuleException(
                    "rows must be 1-" + MAX_ROWS + " and seatsPerRow 1-" + MAX_SEATS_PER_ROW);
        }
        if (!c.startsAt().isAfter(clock.instant())) {
            throw new BusinessRuleException("Event must start in the future");
        }
        Event event = events.save(
                new Event(UUID.randomUUID(), c.name(), c.description(), c.venue(), c.startsAt(), c.price()));
        List<Seat> generated = new ArrayList<>(c.rows() * c.seatsPerRow());
        for (int r = 0; r < c.rows(); r++) {
            for (int n = 1; n <= c.seatsPerRow(); n++) {
                generated.add(new Seat(UUID.randomUUID(), event.id(), (char) ('A' + r) + String.valueOf(n),
                        SeatStatus.AVAILABLE));
            }
        }
        seats.saveAll(generated);
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> list() {
        return events.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Event get(UUID eventId) {
        return events.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> seats(UUID eventId, Optional<SeatStatus> status) {
        get(eventId);
        return seats.findByEvent(eventId, status);
    }

    @Override
    @Transactional
    public Hold hold(UUID eventId, Collection<UUID> seatIds, UUID reservationId) {
        Event event = get(eventId);
        if (!event.startsAt().isAfter(clock.instant())) {
            throw new BusinessRuleException("Event has already started");
        }
        int held = seats.hold(eventId, seatIds, reservationId);
        if (held != seatIds.size()) {
            // the caller's transaction rolls back, so seats held so far are released
            throw new ConflictException("One or more seats are not available");
        }
        return new Hold(event.price());
    }

    @Override
    @Transactional
    public void release(UUID reservationId) {
        seats.release(reservationId);
    }

    @Override
    @Transactional
    public void markSold(UUID reservationId) {
        if (seats.markSold(reservationId) == 0) {
            throw new ConflictException("Seats of this reservation are no longer held");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> seatsOf(UUID reservationId) {
        return seats.findByReservation(reservationId);
    }
}
