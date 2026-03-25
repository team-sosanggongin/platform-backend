package com.platform.sosangongin.services.fcm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmFailureReason {
    /** 토큰 형식이 유효하지 않음 (FCM: INVALID_ARGUMENT, SENDER_ID_MISMATCH) */
    INVALID_TOKEN(FailureCategory.INVALID_TOKEN),
    /** 토큰이 만료되었거나 앱이 삭제되어 더 이상 유효하지 않음 (FCM: UNREGISTERED) */
    UNREGISTERED_TOKEN(FailureCategory.REQUIRES_TOKEN_REFRESH),
    /** FCM 발송 한도 초과 (FCM: QUOTA_EXCEEDED) */
    RATE_LIMIT_EXCEEDED(FailureCategory.RETRYABLE),
    /** FCM 서버 일시 장애 (FCM: UNAVAILABLE) */
    SERVER_UNAVAILABLE(FailureCategory.RETRYABLE),
    /** FCM 서버 내부 오류 (FCM: INTERNAL, THIRD_PARTY_AUTH_ERROR) */
    INTERNAL_ERROR(FailureCategory.NON_RETRYABLE);

    private final FailureCategory category;

    public enum FailureCategory {
        /** 토큰 자체가 잘못됨 — 클라이언트가 토큰을 교정해야 함 */
        INVALID_TOKEN,
        /** 토큰이 만료/해제됨 — 클라이언트가 FCM 토큰을 재발급받아야 함 */
        REQUIRES_TOKEN_REFRESH,
        /** 일시적 오류 — 재시도 가능 */
        RETRYABLE,
        /** 서버 내부 오류 — 재시도해도 해결 불가 */
        NON_RETRYABLE
    }
}
