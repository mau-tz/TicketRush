package com.mautz.ticketrush.ticketrush.events.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.domain.Event;

public interface EventRepositoryPort {

    Event save(Event event);

    Optional<Event> findById(UUID id);

    List<Event> findAll();
}
