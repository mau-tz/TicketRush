package com.mautz.ticketrush.ticketrush.booking.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.booking")
public record BookingProperties(@DefaultValue("10") int holdMinutes, @DefaultValue("6") int maxSeats) {
}
