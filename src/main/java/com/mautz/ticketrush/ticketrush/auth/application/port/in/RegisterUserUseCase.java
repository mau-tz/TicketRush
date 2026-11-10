package com.mautz.ticketrush.ticketrush.auth.application.port.in;

import com.mautz.ticketrush.ticketrush.auth.domain.User;

public interface RegisterUserUseCase {

    User register(Command command);

    record Command(String email, String password, String fullName) {
    }
}
