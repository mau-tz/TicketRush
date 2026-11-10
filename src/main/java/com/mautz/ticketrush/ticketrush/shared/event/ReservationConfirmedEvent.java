package com.mautz.ticketrush.ticketrush.shared.event;

import java.util.List;
import java.util.UUID;

public record ReservationConfirmedEvent(UUID reservationId, UUID userId, String eventName, List<TicketInfo> tickets) {

    public record TicketInfo(String code, String seatLabel) {
    }
}
