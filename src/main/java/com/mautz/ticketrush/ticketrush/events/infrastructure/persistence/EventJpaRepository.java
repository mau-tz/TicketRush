package com.mautz.ticketrush.ticketrush.events.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {

    List<EventEntity> findAllByOrderByStartsAtAsc();
}
