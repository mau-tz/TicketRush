package com.mautz.ticketrush.ticketrush.events.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Event(UUID id, String name, String description, String venue, Instant startsAt, BigDecimal price) {
}
