package com.jamin8r.identity.api;

import java.net.URI;
import java.util.UUID;

import com.jamin8r.identity.UserDirectory;
import com.jamin8r.identity.UserRegistrationService;
import com.jamin8r.identity.UserSnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
class UserController {

    private final UserRegistrationService registration;
    private final UserDirectory users;

    UserController(UserRegistrationService registration, UserDirectory users) {
        this.registration = registration;
        this.users = users;
    }

    @PostMapping
    ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationHeader) {
        UserSnapshot user = registration.register(request.username(), correlationId(correlationHeader));
        return ResponseEntity.created(URI.create("/v1/users/" + user.id()))
                .body(UserResponse.from(user));
    }

    @GetMapping("/{userId}")
    UserResponse get(@PathVariable UUID userId) {
        return UserResponse.from(users.get(userId));
    }

    private static UUID correlationId(String value) {
        if (value == null || value.isBlank()) return UUID.randomUUID();
        try { return UUID.fromString(value); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("X-Correlation-Id must be a UUID"); }
    }

    record RegisterUserRequest(
            @NotBlank @Size(min = 3, max = 32) String username) {
    }

    record UserResponse(UUID id, String username, java.time.Instant createdAt) {
        static UserResponse from(UserSnapshot user) {
            return new UserResponse(user.id(), user.username(), user.createdAt());
        }
    }
}
