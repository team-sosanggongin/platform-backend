package com.platform.sosangongin.api.error;

import com.platform.sosangongin.errors.AccessDeniedException;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.InvalidTokenException;
import com.platform.sosangongin.errors.PlatFormBusinessError;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String CLIENT_ERROR_MESSAGE = "잘못된 접근입니다.";
    private final MeterRegistry meterRegistry;

    public GlobalExceptionHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: [{}] {}", e.getEntityType(), e.getUniqueId());
        incrementCounter("entity_not_found", e.getEntityType().name());

        return badRequest();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: [{}] {}", e.getEntityType(), e.getResourceId());
        incrementCounter("access_denied", e.getEntityType().name());

        return badRequest();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        log.warn("Invalid token: [{}] {}", e.getUsage(), e.getMessage());
        incrementCounter("invalid_token", e.getUsage() != null ? e.getUsage().name() : "UNKNOWN");

        return badRequest();
    }

    @ExceptionHandler(PlatFormBusinessError.class)
    public ResponseEntity<ErrorResponse> handleBusinessError(PlatFormBusinessError e) {
        log.warn("Business error: {}", e.getMessage());
        incrementCounter("business_error", e.getClass().getSimpleName());

        return badRequest();
    }

    private ResponseEntity<ErrorResponse> badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .message(CLIENT_ERROR_MESSAGE)
                        .build());
    }

    private void incrementCounter(String type, String detail) {
        Counter.builder("platform.exception")
                .tag("type", type)
                .tag("detail", detail)
                .register(meterRegistry)
                .increment();
    }
}
