package com.mautz.ticketrush.ticketrush.auth.application.port.in;

import java.time.Instant;

public interface AuthenticateUserUseCase {

    Token authenticate(Command command);

    record Command(String email, String password) {
    }

    record Token(String value, Instant expiresAt) {
    }
}
