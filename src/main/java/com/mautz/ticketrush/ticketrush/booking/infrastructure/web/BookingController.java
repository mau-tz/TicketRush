package com.mautz.ticketrush.ticketrush.booking.infrastructure.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.application.port.in.ManageReservationUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ReserveSeatsUseCase;
import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.booking.domain.Ticket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
class BookingController {

    private final ReserveSeatsUseCase reserve;
    private final ManageReservationUseCase manage;

    BookingController(ReserveSeatsUseCase reserve, ManageReservationUseCase manage) {
        this.reserve = reserve;
        this.manage = manage;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ReservationResponse reserve(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ReserveRequest request) {
        return ReservationResponse.from(reserve.reserve(
                new ReserveSeatsUseCase.Command(userId(jwt), request.eventId(), request.seatIds())));
    }

    @GetMapping("/{id}")
    ReservationResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return ReservationResponse.from(manage.get(id, userId(jwt)));
    }

    @PostMapping("/{id}/cancel")
    ReservationResponse cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return ReservationResponse.from(manage.cancel(id, userId(jwt)));
    }

    @GetMapping("/{id}/tickets")
    List<TicketResponse> tickets(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return manage.tickets(id, userId(jwt)).stream().map(TicketResponse::from).toList();
    }

    private static UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    record ReserveRequest(@NotNull UUID eventId, @NotEmpty List<@NotNull UUID> seatIds) {
    }

    record ReservationResponse(UUID id, UUID eventId, List<UUID> seatIds, BigDecimal total, String status,
            Instant expiresAt) {
        static ReservationResponse from(Reservation r) {
            return new ReservationResponse(r.id(), r.eventId(), r.seatIds(), r.total(), r.status().name(),
                    r.expiresAt());
        }
    }

    record TicketResponse(UUID id, String seatLabel, String code) {
        static TicketResponse from(Ticket t) {
            return new TicketResponse(t.id(), t.seatLabel(), t.code());
        }
    }
}
