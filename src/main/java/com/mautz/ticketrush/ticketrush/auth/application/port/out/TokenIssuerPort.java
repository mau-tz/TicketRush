package com.mautz.ticketrush.ticketrush.auth.application.port.out;

import com.mautz.ticketrush.ticketrush.auth.application.port.in.AuthenticateUserUseCase;
import com.mautz.ticketrush.ticketrush.auth.domain.User;

public interface TokenIssuerPort {

    AuthenticateUserUseCase.Token issue(User user);
}
