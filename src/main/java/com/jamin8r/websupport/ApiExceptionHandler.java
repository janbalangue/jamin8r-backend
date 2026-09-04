package com.jamin8r.websupport;

import java.time.Instant;
import java.util.Map;

import com.jamin8r.identity.UserNotFoundException;
import com.jamin8r.identity.UsernameTakenException;
import com.jamin8r.profile.ProfileNotFoundException;
import com.jamin8r.social.SelfFollowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiError> userNotFound(UserNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "user_not_found", ex.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<ApiError> profileNotFound(ProfileNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "profile_not_found", ex.getMessage());
    }

    @ExceptionHandler(UsernameTakenException.class)
    ResponseEntity<ApiError> usernameTaken(UsernameTakenException ex) {
        return error(HttpStatus.CONFLICT, "username_taken", ex.getMessage());
    }

    @ExceptionHandler(SelfFollowException.class)
    ResponseEntity<ApiError> selfFollow(SelfFollowException ex) {
        return error(HttpStatus.BAD_REQUEST, "self_follow", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, "invalid_request", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        field -> field.getField(),
                        field -> field.getDefaultMessage() == null ? "invalid" : field.getDefaultMessage(),
                        (left, right) -> left));
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), 400, "validation_failed", "Request validation failed", fields));
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), code, message, Map.of()));
    }

    record ApiError(Instant timestamp, int status, String code, String message, Map<String, String> fields) {
    }
}
