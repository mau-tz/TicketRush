package com.mautz.ticketrush.ticketrush.booking.application.service;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.application.BookingProperties;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ExpireReservationsUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ManageReservationUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ReserveSeatsUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.out.ReservationRepositoryPort;
import com.mautz.ticketrush.ticketrush.booking.application.port.out.TicketRepositoryPort;
import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.booking.domain.Ticket;
import com.mautz.ticketrush.ticketrush.events.application.port.in.ManageEventsUseCase;
import com.mautz.ticketrush.ticketrush.events.application.port.in.SeatInventoryApi;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.shared.event.ReservationConfirmedEvent;
import com.mautz.ticketrush.ticketrush.shared.exception.BusinessRuleException;
import com.mautz.ticketrush.ticketrush.shared.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class BookingService implements ReserveSeatsUseCase, ManageReservationUseCase, ExpireReservationsUseCase {

    private final ReservationRepositoryPort reservations;
    private final TicketRepositoryPort tickets;
    private final SeatInventoryApi inventory;
    private final ManageEventsUseCase events;
    private final ApplicationEventPublisher publisher;
    private final BookingProperties properties;
    private final Clock clock;

    BookingService(ReservationRepositoryPort reservations, TicketRepositoryPort tickets, SeatInventoryApi inventory,
            ManageEventsUseCase events, ApplicationEventPublisher publisher, BookingProperties properties,
            Clock clock) {
        this.reservations = reservations;
        this.tickets = tickets;
        this.inventory = inventory;
        this.events = events;
        this.publisher = publisher;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Reservation reserve(ReserveSeatsUseCase.Command command) {
        List<UUID> seatIds = command.seatIds().stream().distinct().toList();
        if (seatIds.isEmpty() || seatIds.size() > properties.maxSeats()) {
            throw new BusinessRuleException("You can reserve between 1 and " + properties.maxSeats() + " seats");
        }
        UUID reservationId = UUID.randomUUID();
        // Atomic hold: throws ConflictException (rolling this transaction back) if any seat was taken.
        var hold = inventory.hold(command.eventId(), seatIds, reservationId);
        Reservation reservation = Reservation.create(reservationId, command.userId(), command.eventId(), seatIds,
                hold.unitPrice(), Duration.ofMinutes(properties.holdMinutes()), clock.instant());
        return reservations.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation get(UUID reservationId, UUID userId) {
        return reservations.findById(reservationId).filter(r -> r.userId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }

    @Override
    @Transactional
    public Reservation cancel(UUID reservationId, UUID userId) {
        Reservation reservation = lockOwned(reservationId, userId);
        reservation.cancel();
        inventory.release(reservationId);
        return reservations.save(reservation);
    }

    @Override
    @Transactional
    public Reservation confirm(UUID reservationId, UUID userId) {
        Reservation reservation = lockOwned(reservationId, userId);
        reservation.confirm(clock.instant());
        inventory.markSold(reservationId);

        List<Seat> sold = inventory.seatsOf(reservationId);
        List<Ticket> issued = sold.stream()
                .map(s -> new Ticket(UUID.randomUUID(), reservationId, s.id(), s.label(),
                        UUID.randomUUID().toString().replace("-", "")))
                .toList();
        tickets.saveAll(issued);
        Reservation saved = reservations.save(reservation);

        publisher.publishEvent(new ReservationConfirmedEvent(reservationId, userId,
                events.get(reservation.eventId()).name(),
                issued.stream().map(t -> new ReservationConfirmedEvent.TicketInfo(t.code(), t.seatLabel())).toList()));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> tickets(UUID reservationId, UUID userId) {
        get(reservationId, userId);
        return tickets.findByReservation(reservationId);
    }

    @Override
    @Transactional
    public int expireOverdue() {
        List<Reservation> overdue = reservations.findOverduePending(clock.instant());
        for (Reservation r : overdue) {
            // re-read under lock: it may have been confirmed or cancelled since the query
            reservations.findByIdForUpdate(r.id()).filter(l -> l.isOverdue(clock.instant())).ifPresent(l -> {
                l.expire();
                inventory.release(l.id());
                reservations.save(l);
            });
        }
        return overdue.size();
    }

    private Reservation lockOwned(UUID reservationId, UUID userId) {
        return reservations.findByIdForUpdate(reservationId).filter(r -> r.userId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }
}
