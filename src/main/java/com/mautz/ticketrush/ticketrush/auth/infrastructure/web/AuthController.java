package com.mautz.ticketrush.ticketrush.auth.infrastructure.web;

import java.time.Instant;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.application.port.in.AuthenticateUserUseCase;
import com.mautz.ticketrush.ticketrush.auth.application.port.in.RegisterUserUseCase;
import com.mautz.ticketrush.ticketrush.auth.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final RegisterUserUseCase register;
    private final AuthenticateUserUseCase authenticate;

    AuthController(RegisterUserUseCase register, AuthenticateUserUseCase authenticate) {
        this.register = register;
        this.authenticate = authenticate;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = register.register(
                new RegisterUserUseCase.Command(request.email(), request.password(), request.fullName()));
        return new UserResponse(user.id(), user.email(), user.fullName(), user.role().name());
    }

    @PostMapping("/login")
    TokenResponse login(@Valid @RequestBody LoginRequest request) {
        var token = authenticate.authenticate(new AuthenticateUserUseCase.Command(request.email(), request.password()));
        return new TokenResponse(token.value(), "Bearer", token.expiresAt());
    }

    record RegisterRequest(@NotBlank @Email String email,
                           @NotBlank @Size(min = 8, max = 100) String password,
                           @NotBlank @Size(max = 255) String fullName) {
    }

    record LoginRequest(@NotBlank String email, @NotBlank String password) {
    }

    record UserResponse(UUID id, String email, String fullName, String role) {
    }

    record TokenResponse(String accessToken, String tokenType, Instant expiresAt) {
    }
}
