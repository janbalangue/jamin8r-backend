package com.jamin8r.profile.internal;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
