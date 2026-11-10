package com.mautz.ticketrush.ticketrush.events.infrastructure.persistence;

import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
class SeatEntity {

    @Id
    private UUID id;
    @Column(name = "event_id")
    private UUID eventId;
    private String label;
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    @Column(name = "reservation_id")
    private UUID reservationId;
    /** A null version marks the entity as new, so bulk inserts skip the existence SELECT per seat. */
    @Version
    private Long version;
}
