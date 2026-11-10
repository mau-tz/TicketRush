package com.mautz.ticketrush.ticketrush.events.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.application.port.out.EventRepositoryPort;
import com.mautz.ticketrush.ticketrush.events.application.port.out.SeatRepositoryPort;
import com.mautz.ticketrush.ticketrush.events.domain.Event;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import org.springframework.stereotype.Component;

@Component
class EventPersistenceAdapter implements EventRepositoryPort, SeatRepositoryPort {

    private final EventJpaRepository eventJpa;
    private final SeatJpaRepository seatJpa;

    EventPersistenceAdapter(EventJpaRepository eventJpa, SeatJpaRepository seatJpa) {
        this.eventJpa = eventJpa;
        this.seatJpa = seatJpa;
    }

    @Override
    public Event save(Event event) {
        EventEntity e = eventJpa.findById(event.id()).orElseGet(EventEntity::new);
        e.setId(event.id());
        e.setName(event.name());
        e.setDescription(event.description());
        e.setVenue(event.venue());
        e.setStartsAt(event.startsAt());
        e.setPrice(event.price());
        return toDomain(eventJpa.save(e));
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return eventJpa.findById(id).map(EventPersistenceAdapter::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return eventJpa.findAllByOrderByStartsAtAsc().stream().map(EventPersistenceAdapter::toDomain).toList();
    }

    @Override
    public void saveAll(List<Seat> seats) {
        seatJpa.saveAll(seats.stream().map(s -> {
            SeatEntity e = new SeatEntity();
            e.setId(s.id());
            e.setEventId(s.eventId());
            e.setLabel(s.label());
            e.setStatus(s.status());
            return e;
        }).toList());
    }

    @Override
    public List<Seat> findByEvent(UUID eventId, Optional<SeatStatus> status) {
        List<SeatEntity> found = status.isPresent()
                ? seatJpa.findByEventIdAndStatusOrderByLabel(eventId, status.get())
                : seatJpa.findByEventIdOrderByLabel(eventId);
        return found.stream().map(EventPersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<Seat> findByReservation(UUID reservationId) {
        return seatJpa.findByReservationIdOrderByLabel(reservationId).stream()
                .map(EventPersistenceAdapter::toDomain).toList();
    }

    @Override
    public int hold(UUID eventId, Collection<UUID> seatIds, UUID reservationId) {
        return seatJpa.transition(eventId, seatIds, SeatStatus.AVAILABLE, SeatStatus.HELD, reservationId);
    }

    @Override
    public int release(UUID reservationId) {
        return seatJpa.transitionByReservation(reservationId, SeatStatus.HELD, SeatStatus.AVAILABLE, null);
    }

    @Override
    public int markSold(UUID reservationId) {
        return seatJpa.transitionByReservation(reservationId, SeatStatus.HELD, SeatStatus.SOLD, reservationId);
    }

    private static Event toDomain(EventEntity e) {
        return new Event(e.getId(), e.getName(), e.getDescription(), e.getVenue(), e.getStartsAt(), e.getPrice());
    }

    private static Seat toDomain(SeatEntity e) {
        return new Seat(e.getId(), e.getEventId(), e.getLabel(), e.getStatus());
    }
}
