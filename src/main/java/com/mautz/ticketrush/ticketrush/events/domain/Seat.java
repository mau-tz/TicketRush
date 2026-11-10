package com.mautz.ticketrush.ticketrush.events.domain;

import java.util.UUID;

public record Seat(UUID id, UUID eventId, String label, SeatStatus status) {
}
