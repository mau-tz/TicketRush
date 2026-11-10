package com.mautz.ticketrush.ticketrush.auth.application.service;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.application.port.in.AuthenticateUserUseCase;
import com.mautz.ticketrush.ticketrush.auth.application.port.in.RegisterUserUseCase;
import com.mautz.ticketrush.ticketrush.auth.application.port.in.UserQueryApi;
import com.mautz.ticketrush.ticketrush.auth.application.port.out.TokenIssuerPort;
import com.mautz.ticketrush.ticketrush.auth.application.port.out.UserRepositoryPort;
import com.mautz.ticketrush.ticketrush.auth.domain.Role;
import com.mautz.ticketrush.ticketrush.auth.domain.User;
import com.mautz.ticketrush.ticketrush.shared.exception.ConflictException;
import com.mautz.ticketrush.ticketrush.shared.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuthService implements RegisterUserUseCase, AuthenticateUserUseCase, UserQueryApi {

    private final UserRepositoryPort users;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuerPort tokenIssuer;

    AuthService(UserRepositoryPort users, PasswordEncoder passwordEncoder, TokenIssuerPort tokenIssuer) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    @Transactional
    public User register(RegisterUserUseCase.Command command) {
        String email = normalize(command.email());
        if (users.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }
        return users.save(new User(UUID.randomUUID(), email, passwordEncoder.encode(command.password()),
                command.fullName().trim(), Role.USER));
    }

    @Override
    @Transactional(readOnly = true)
    public Token authenticate(AuthenticateUserUseCase.Command command) {
        User user = users.findByEmail(normalize(command.email()))
                .filter(u -> passwordEncoder.matches(command.password(), u.passwordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return tokenIssuer.issue(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findEmailById(UUID userId) {
        return users.findById(userId).map(User::email);
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
