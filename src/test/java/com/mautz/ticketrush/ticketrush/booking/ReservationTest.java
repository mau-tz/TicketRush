package com.mautz.ticketrush.ticketrush.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.booking.domain.Reservation;
import com.mautz.ticketrush.ticketrush.booking.domain.ReservationStatus;
import com.mautz.ticketrush.ticketrush.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private static final Instant NOW = Instant.parse("2030-01-01T10:00:00Z");

    private Reservation pending() {
        return Reservation.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                List.of(UUID.randomUUID(), UUID.randomUUID()), new BigDecimal("25.00"), Duration.ofMinutes(10), NOW);
    }

    @Test
    void totalIsUnitPriceTimesSeats() {
        assertThat(pending().total()).isEqualByComparingTo("50.00");
    }

    @Test
    void confirmsWhileHoldIsValid() {
        Reservation r = pending();
        r.confirm(NOW.plusSeconds(60));
        assertThat(r.status()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void cannotConfirmAfterHoldExpired() {
        assertThatThrownBy(() -> pending().confirm(NOW.plus(Duration.ofMinutes(11))))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotCancelAConfirmedReservation() {
        Reservation r = pending();
        r.confirm(NOW);
        assertThatThrownBy(r::cancel).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void overdueOnlyWhenPendingAndPastExpiry() {
        Reservation r = pending();
        assertThat(r.isOverdue(NOW)).isFalse();
        assertThat(r.isOverdue(NOW.plus(Duration.ofMinutes(10)))).isTrue();
    }
}
