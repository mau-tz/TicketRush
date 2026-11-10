package com.mautz.ticketrush.ticketrush.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.auth.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
class UserEntity {

    @Id
    private UUID id;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "full_name")
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
