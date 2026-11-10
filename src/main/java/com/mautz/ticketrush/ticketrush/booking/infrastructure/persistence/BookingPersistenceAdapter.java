package com.mautz.ticketrush.ticketrush.booking.infrastructure.persistence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.application.port.out.ReservationRepositoryPort;
import com.mautz.ticketrush.ticketrush.booking.application.port.out.TicketRepositoryPort;
import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.booking.domain.ReservationStatus;
import com.mautz.ticketrush.ticketrush.booking.domain.Ticket;
import org.springframework.stereotype.Component;

@Component
class BookingPersistenceAdapter implements ReservationRepositoryPort, TicketRepositoryPort {

    private final ReservationJpaRepository reservationJpa;
    private final TicketJpaRepository ticketJpa;

    BookingPersistenceAdapter(ReservationJpaRepository reservationJpa, TicketJpaRepository ticketJpa) {
        this.reservationJpa = reservationJpa;
        this.ticketJpa = ticketJpa;
    }

    @Override
    public Reservation save(Reservation r) {
        ReservationEntity e = reservationJpa.findById(r.id()).orElseGet(ReservationEntity::new);
        e.setId(r.id());
        e.setUserId(r.userId());
        e.setEventId(r.eventId());
        e.setStatus(r.status());
        e.setTotal(r.total());
        e.setExpiresAt(r.expiresAt());
        e.setCreatedAt(r.createdAt());
        e.setSeatIds(new ArrayList<>(r.seatIds()));
        return toDomain(reservationJpa.save(e));
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        return reservationJpa.findById(id).map(BookingPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<Reservation> findByIdForUpdate(UUID id) {
        return reservationJpa.findByIdForUpdate(id).map(BookingPersistenceAdapter::toDomain);
    }

    @Override
    public List<Reservation> findOverduePending(Instant now) {
        return reservationJpa.findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now).stream()
                .map(BookingPersistenceAdapter::toDomain).toList();
    }

    @Override
    public void saveAll(List<Ticket> tickets) {
        ticketJpa.saveAll(tickets.stream().map(t -> {
            TicketEntity e = new TicketEntity();
            e.setId(t.id());
            e.setReservationId(t.reservationId());
            e.setSeatId(t.seatId());
            e.setSeatLabel(t.seatLabel());
            e.setCode(t.code());
            return e;
        }).toList());
    }

    @Override
    public List<Ticket> findByReservation(UUID reservationId) {
        return ticketJpa.findByReservationIdOrderBySeatLabel(reservationId).stream()
                .map(e -> new Ticket(e.getId(), e.getReservationId(), e.getSeatId(), e.getSeatLabel(), e.getCode()))
                .toList();
    }

    private static Reservation toDomain(ReservationEntity e) {
        return new Reservation(e.getId(), e.getUserId(), e.getEventId(), e.getSeatIds(), e.getTotal(),
                e.getStatus(), e.getExpiresAt(), e.getCreatedAt());
    }
}
