package com.mautz.ticketrush.ticketrush.auth.infrastructure.security;

import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.application.port.out.UserRepositoryPort;
import com.mautz.ticketrush.ticketrush.auth.domain.Role;
import com.mautz.ticketrush.ticketrush.auth.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Creates the initial admin account on first start. */
@Configuration
class AdminSeeder {

    @Bean
    ApplicationRunner seedAdmin(UserRepositoryPort users, PasswordEncoder encoder,
            @Value("${app.admin.email}") String email, @Value("${app.admin.password}") String password) {
        return args -> {
            if (!users.existsByEmail(email)) {
                users.save(new User(UUID.randomUUID(), email, encoder.encode(password), "Administrator", Role.ADMIN));
            }
        };
    }
}
