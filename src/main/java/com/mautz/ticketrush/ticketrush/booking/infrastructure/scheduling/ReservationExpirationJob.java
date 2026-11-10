package com.mautz.ticketrush.ticketrush.booking.infrastructure.scheduling;

import com.mautz.ticketrush.ticketrush.booking.application.port.in.ExpireReservationsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class ReservationExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpirationJob.class);

    private final ExpireReservationsUseCase expire;

    ReservationExpirationJob(ExpireReservationsUseCase expire) {
        this.expire = expire;
    }

    @Scheduled(fixedDelayString = "PT30S")
    void run() {
        int expired = expire.expireOverdue();
        if (expired > 0) {
            log.info("Expired {} overdue reservations", expired);
        }
    }
}
