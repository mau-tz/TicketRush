package com.mautz.ticketrush.ticketrush.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, @DefaultValue("60") long expirationMinutes) {
}
