package com.mautz.ticketrush.ticketrush.events.infrastructure.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.events.application.port.in.ManageEventsUseCase;
import com.mautz.ticketrush.ticketrush.events.domain.Event;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
class EventController {

    private final ManageEventsUseCase events;

    EventController(ManageEventsUseCase events) {
        this.events = events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventResponse create(@Valid @RequestBody CreateEventRequest r) {
        return EventResponse.from(events.create(new ManageEventsUseCase.Command(r.name(), r.description(),
                r.venue(), r.startsAt(), r.price(), r.rows(), r.seatsPerRow())));
    }

    @GetMapping
    List<EventResponse> list() {
        return events.list().stream().map(EventResponse::from).toList();
    }

    @GetMapping("/{id}")
    EventResponse get(@PathVariable UUID id) {
        return EventResponse.from(events.get(id));
    }

    @GetMapping("/{id}/seats")
    List<SeatResponse> seats(@PathVariable UUID id, @RequestParam Optional<SeatStatus> status) {
        return events.seats(id, status).stream().map(SeatResponse::from).toList();
    }

    record CreateEventRequest(@NotBlank String name,
                              String description,
                              @NotBlank String venue,
                              @NotNull Instant startsAt,
                              @NotNull @DecimalMin("0.00") BigDecimal price,
                              @Min(1) @Max(26) int rows,
                              @Min(1) @Max(100) int seatsPerRow) {
    }

    record EventResponse(UUID id, String name, String description, String venue, Instant startsAt,
            BigDecimal price) {
        static EventResponse from(Event e) {
            return new EventResponse(e.id(), e.name(), e.description(), e.venue(), e.startsAt(), e.price());
        }
    }

    record SeatResponse(UUID id, String label, SeatStatus status) {
        static SeatResponse from(Seat s) {
            return new SeatResponse(s.id(), s.label(), s.status());
        }
    }
}
