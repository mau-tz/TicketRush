package com.mautz.ticketrush.ticketrush.events.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SeatJpaRepository extends JpaRepository<SeatEntity, UUID> {

    List<SeatEntity> findByEventIdOrderByLabel(UUID eventId);

    List<SeatEntity> findByEventIdAndStatusOrderByLabel(UUID eventId, SeatStatus status);

    List<SeatEntity> findByReservationIdOrderByLabel(UUID reservationId);

    /** The status predicate in the WHERE clause makes the hold atomic: concurrent holders cannot both win. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SeatEntity s set s.status = :to, s.reservationId = :reservationId
            where s.eventId = :eventId and s.id in :ids and s.status = :from
            """)
    int transition(@Param("eventId") UUID eventId, @Param("ids") Collection<UUID> ids,
            @Param("from") SeatStatus from, @Param("to") SeatStatus to, @Param("reservationId") UUID reservationId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SeatEntity s set s.status = :to, s.reservationId = :newReservationId
            where s.reservationId = :reservationId and s.status = :from
            """)
    int transitionByReservation(@Param("reservationId") UUID reservationId, @Param("from") SeatStatus from,
            @Param("to") SeatStatus to, @Param("newReservationId") UUID newReservationId);
}
