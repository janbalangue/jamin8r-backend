package com.jamin8r.social.api;

import java.util.List;
import java.util.UUID;

import com.jamin8r.social.FollowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
class FollowController {

    private final FollowService follows;

    FollowController(FollowService follows) {
        this.follows = follows;
    }

    @PostMapping("/follows")
    ResponseEntity<FollowResponse> follow(
            @Valid @RequestBody FollowRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationHeader) {
        var result = follows.follow(request.followerId(), request.followedId(), correlationId(correlationHeader));
        return ResponseEntity.accepted().body(new FollowResponse(result.created()));
    }

    @GetMapping("/users/{userId}/following")
    UserIdsResponse following(@PathVariable UUID userId) {
        return new UserIdsResponse(follows.following(userId));
    }

    @GetMapping("/users/{userId}/followers")
    UserIdsResponse followers(@PathVariable UUID userId) {
        return new UserIdsResponse(follows.followers(userId));
    }

    private static UUID correlationId(String value) {
        if (value == null || value.isBlank()) return UUID.randomUUID();
        try { return UUID.fromString(value); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("X-Correlation-Id must be a UUID"); }
    }

    record FollowRequest(@NotNull UUID followerId, @NotNull UUID followedId) {
    }

    record FollowResponse(boolean created) {
    }

    record UserIdsResponse(List<UUID> userIds) {
    }
}
