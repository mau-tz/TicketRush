package com.mautz.ticketrush.ticketrush.booking.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

    List<TicketEntity> findByReservationIdOrderBySeatLabel(UUID reservationId);
}
