package com.mautz.ticketrush.ticketrush.booking.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
class TicketEntity {

    @Id
    private UUID id;
    @Column(name = "reservation_id")
    private UUID reservationId;
    @Column(name = "seat_id")
    private UUID seatId;
    @Column(name = "seat_label")
    private String seatLabel;
    private String code;
}
