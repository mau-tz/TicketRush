package com.mautz.ticketrush.ticketrush.booking.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.ReservationStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
class ReservationEntity {

    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "event_id")
    private UUID eventId;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private BigDecimal total;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "created_at")
    private Instant createdAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_seats", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "seat_id")
    private List<UUID> seatIds = new ArrayList<>();
}
