package com.jamin8r.profile.api;

import java.util.UUID;

import com.jamin8r.profile.ProfileService;
import com.jamin8r.profile.ProfileSnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users/{userId}/profile")
class ProfileController {

    private final ProfileService profiles;

    ProfileController(ProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    ProfileResponse get(@PathVariable UUID userId) {
        return ProfileResponse.from(profiles.get(userId));
    }

    @PutMapping
    ProfileResponse upsert(
            @PathVariable UUID userId,
            @Valid @RequestBody UpsertProfileRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationHeader) {
        return ProfileResponse.from(profiles.upsert(userId, request.displayName(), request.bio(),
                request.location(), correlationId(correlationHeader)));
    }

    private static UUID correlationId(String value) {
        if (value == null || value.isBlank()) return UUID.randomUUID();
        try { return UUID.fromString(value); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("X-Correlation-Id must be a UUID"); }
    }

    record UpsertProfileRequest(
            @NotBlank @Size(max = 80) String displayName,
            @Size(max = 500) String bio,
            @Size(max = 120) String location) {
    }

    record ProfileResponse(UUID userId, String displayName, String bio, String location, java.time.Instant updatedAt) {
        static ProfileResponse from(ProfileSnapshot profile) {
            return new ProfileResponse(profile.userId(), profile.displayName(), profile.bio(),
                    profile.location(), profile.updatedAt());
        }
    }
}
