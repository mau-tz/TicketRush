package com.mautz.ticketrush.ticketrush.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.domain.User;

public interface UserRepositoryPort {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);

    User save(User user);
}
