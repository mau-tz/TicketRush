package com.mautz.ticketrush.ticketrush.events.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
class EventEntity {

    @Id
    private UUID id;
    private String name;
    private String description;
    private String venue;
    @Column(name = "starts_at")
    private Instant startsAt;
    private BigDecimal price;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
