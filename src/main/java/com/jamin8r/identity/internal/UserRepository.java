package com.jamin8r.identity.internal;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByUsernameIgnoreCase(String username);
}
