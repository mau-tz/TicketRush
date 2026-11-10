package com.mautz.ticketrush.ticketrush.auth.infrastructure.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.mautz.ticketrush.ticketrush.auth.application.port.in.AuthenticateUserUseCase.Token;
import com.mautz.ticketrush.ticketrush.auth.application.port.out.TokenIssuerPort;
import com.mautz.ticketrush.ticketrush.auth.domain.User;
import com.mautz.ticketrush.ticketrush.shared.config.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
class JwtTokenIssuer implements TokenIssuerPort {

    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;

    JwtTokenIssuer(JwtEncoder encoder, JwtProperties properties, Clock clock) {
        this.encoder = encoder;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public Token issue(User user) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(Duration.ofMinutes(properties.expirationMinutes()));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.id().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("email", user.email())
                .claim("roles", List.of(user.role().name()))
                .build();
        String value = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new Token(value, expiresAt);
    }
}
