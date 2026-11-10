package com.mautz.ticketrush.ticketrush.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mautz.ticketrush.ticketrush.TestcontainersConfiguration;
import com.mautz.ticketrush.ticketrush.auth.application.port.in.RegisterUserUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ManageReservationUseCase;
import com.mautz.ticketrush.ticketrush.booking.application.port.in.ReserveSeatsUseCase;
import com.mautz.ticketrush.ticketrush.booking.domain.ReservationStatus;
import com.mautz.ticketrush.ticketrush.events.application.port.in.ManageEventsUseCase;
import com.mautz.ticketrush.ticketrush.events.domain.Event;
import com.mautz.ticketrush.ticketrush.events.domain.Seat;
import com.mautz.ticketrush.ticketrush.events.domain.SeatStatus;
import com.mautz.ticketrush.ticketrush.payment.application.port.in.PayReservationUseCase;
import com.mautz.ticketrush.ticketrush.payment.domain.PaymentStatus;
import com.mautz.ticketrush.ticketrush.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BookingFlowIntegrationTest {

    @Autowired RegisterUserUseCase register;
    @Autowired ManageEventsUseCase events;
    @Autowired ReserveSeatsUseCase reserve;
    @Autowired ManageReservationUseCase manage;
    @Autowired PayReservationUseCase pay;

    private UUID newUser() {
        return register.register(new RegisterUserUseCase.Command(
                UUID.randomUUID() + "@test.com", "password123", "Test User")).id();
    }

    private Event newEvent() {
        return events.create(new ManageEventsUseCase.Command("Concert", "desc", "Arena",
                Instant.now().plus(7, ChronoUnit.DAYS), new BigDecimal("40.00"), 2, 5));
    }

    @Test
    void onlyOneOfManyConcurrentBuyersGetsTheSameSeat() throws Exception {
        Event event = newEvent();
        UUID seat = events.seats(event.id(), java.util.Optional.empty()).get(0).id();
        int buyers = 20;
        List<UUID> users = new ArrayList<>();
        for (int i = 0; i < buyers; i++) {
            users.add(newUser());
        }

        ExecutorService pool = Executors.newFixedThreadPool(buyers);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (UUID user : users) {
            results.add(pool.submit(() -> {
                start.await();
                try {
                    reserve.reserve(new ReserveSeatsUseCase.Command(user, event.id(), List.of(seat)));
                    return true;
                } catch (ConflictException e) {
                    return false;
                }
            }));
        }
        start.countDown();
        long winners = 0;
        for (Future<Boolean> f : results) {
            if (f.get()) {
                winners++;
            }
        }
        pool.shutdown();

        assertThat(winners).isEqualTo(1);
        assertThat(events.seats(event.id(), java.util.Optional.of(SeatStatus.HELD))).hasSize(1);
    }

    @Test
    void payingConfirmsAndRepeatingTheSameIdempotencyKeyDoesNotChargeTwice() {
        Event event = newEvent();
        UUID user = newUser();
        List<Seat> seats = events.seats(event.id(), java.util.Optional.empty());
        var reservation = reserve.reserve(new ReserveSeatsUseCase.Command(user, event.id(),
                List.of(seats.get(0).id(), seats.get(1).id())));
        assertThat(reservation.total()).isEqualByComparingTo("80.00");

        var cmd = new PayReservationUseCase.Command(user, reservation.id(), "tok_ok", "key-" + UUID.randomUUID());
        var first = pay.pay(cmd);
        var second = pay.pay(cmd);

        assertThat(first.status()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(second.id()).isEqualTo(first.id());
        assertThat(manage.get(reservation.id(), user).status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(manage.tickets(reservation.id(), user)).hasSize(2);
        assertThat(events.seats(event.id(), java.util.Optional.of(SeatStatus.SOLD))).hasSize(2);
    }

    @Test
    void cancellingFreesTheSeatsAndOthersCannotSeeTheReservation() {
        Event event = newEvent();
        UUID owner = newUser();
        UUID seat = events.seats(event.id(), java.util.Optional.empty()).get(0).id();
        var reservation = reserve.reserve(new ReserveSeatsUseCase.Command(owner, event.id(), List.of(seat)));

        assertThatThrownBy(() -> manage.get(reservation.id(), newUser()))
                .isInstanceOf(com.mautz.ticketrush.ticketrush.shared.exception.NotFoundException.class);

        manage.cancel(reservation.id(), owner);
        assertThat(events.seats(event.id(), java.util.Optional.of(SeatStatus.AVAILABLE))).hasSize(10);
    }
}
