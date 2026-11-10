package com.mautz.ticketrush.ticketrush.booking.application.port.in;

public interface ExpireReservationsUseCase {

    /** Expires pending reservations whose hold time ran out and frees their seats. Returns how many. */
    int expireOverdue();
}
