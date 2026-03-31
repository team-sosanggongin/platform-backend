package com.platform.sosangongin.api.error;

import com.platform.sosangongin.errors.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        handler = new GlobalExceptionHandler(meterRegistry);
    }

    @Test
    @DisplayName("EntityNotFoundException 발생 시 400 응답 및 메시지 노출 안 함")
    void handleEntityNotFound() {
        // given
        EntityNotFoundException e = new EntityNotFoundException("user-id-123", EntityType.USER, "user not found");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFound(e);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 접근입니다.");
        assertThat(response.getBody().getMessage()).doesNotContain("user not found");
        assertThat(response.getBody().getError()).isNull();
    }

    @Test
    @DisplayName("AccessDeniedException 발생 시 400 응답 및 메시지 노출 안 함")
    void handleAccessDenied() {
        // given
        AccessDeniedException e = new AccessDeniedException(1L, EntityType.ROLE, "해당 역할에 대한 접근 권한이 없습니다");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(e);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 접근입니다.");
        assertThat(response.getBody().getMessage()).doesNotContain("접근 권한");
    }

    @Test
    @DisplayName("InvalidTokenException 발생 시 400 응답 및 메시지 노출 안 함")
    void handleInvalidToken() {
        // given
        InvalidTokenException e = new InvalidTokenException("token expired", UUID.randomUUID(), "refresh-token", InvalidTokenUsage.INVALID_STATE);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleInvalidToken(e);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 접근입니다.");
        assertThat(response.getBody().getMessage()).doesNotContain("token expired");
    }

    @Test
    @DisplayName("PlatFormBusinessError 발생 시 400 응답 및 메시지 노출 안 함")
    void handleBusinessError() {
        // given
        PlatFormBusinessError e = new PlatFormBusinessError("internal detail message");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleBusinessError(e);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 접근입니다.");
        assertThat(response.getBody().getMessage()).doesNotContain("internal detail");
    }

    // ── 프로메테우스 메트릭 ──

    @Test
    @DisplayName("EntityNotFoundException 발생 시 프로메테우스 카운터 증가")
    void entityNotFound_IncreasesCounter() {
        // given
        EntityNotFoundException e = new EntityNotFoundException("id", EntityType.USER, "msg");

        // when
        handler.handleEntityNotFound(e);
        handler.handleEntityNotFound(e);

        // then
        double count = meterRegistry.counter("platform.exception", "type", "entity_not_found", "detail", "USER").count();
        assertThat(count).isEqualTo(2.0);
    }

    @Test
    @DisplayName("AccessDeniedException 발생 시 프로메테우스 카운터 증가")
    void accessDenied_IncreasesCounter() {
        // given
        AccessDeniedException e = new AccessDeniedException(1L, EntityType.ROLE, "msg");

        // when
        handler.handleAccessDenied(e);

        // then
        double count = meterRegistry.counter("platform.exception", "type", "access_denied", "detail", "ROLE").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    @DisplayName("InvalidTokenException 발생 시 프로메테우스 카운터 증가")
    void invalidToken_IncreasesCounter() {
        // given
        InvalidTokenException e = new InvalidTokenException("msg", "token", InvalidTokenUsage.INVALID_FORMAT);

        // when
        handler.handleInvalidToken(e);

        // then
        double count = meterRegistry.counter("platform.exception", "type", "invalid_token", "detail", "INVALID_FORMAT").count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    @DisplayName("서로 다른 예외 타입은 각각 별도 카운터로 집계")
    void differentExceptions_SeparateCounters() {
        // when
        handler.handleEntityNotFound(new EntityNotFoundException("id", EntityType.USER, "msg"));
        handler.handleAccessDenied(new AccessDeniedException(1L, EntityType.ROLE, "msg"));
        handler.handleEntityNotFound(new EntityNotFoundException("id", EntityType.BUSINESS, "msg"));

        // then
        assertThat(meterRegistry.counter("platform.exception", "type", "entity_not_found", "detail", "USER").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("platform.exception", "type", "entity_not_found", "detail", "BUSINESS").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("platform.exception", "type", "access_denied", "detail", "ROLE").count()).isEqualTo(1.0);
    }
}
