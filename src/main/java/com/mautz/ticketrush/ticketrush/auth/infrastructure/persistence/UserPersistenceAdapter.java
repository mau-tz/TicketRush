package com.mautz.ticketrush.ticketrush.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.application.port.out.UserRepositoryPort;
import com.mautz.ticketrush.ticketrush.auth.domain.User;
import org.springframework.stereotype.Component;

@Component
class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    UserPersistenceAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpa.findById(id).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = jpa.findById(user.id()).orElseGet(UserEntity::new);
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setFullName(user.fullName());
        entity.setRole(user.role());
        return toDomain(jpa.save(entity));
    }

    private static User toDomain(UserEntity e) {
        return new User(e.getId(), e.getEmail(), e.getPasswordHash(), e.getFullName(), e.getRole());
    }
}
