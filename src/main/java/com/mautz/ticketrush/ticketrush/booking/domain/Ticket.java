package com.mautz.ticketrush.ticketrush.booking.domain;

import java.util.UUID;

public record Ticket(UUID id,
                     UUID reservationId,
                     UUID seatId,
                     String seatLabel,
                     String code) {
}
