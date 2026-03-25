package com.platform.sosangongin.services.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmPushServiceImplTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FcmPushServiceImpl fcmPushService;

    private FcmPushRequest request;

    @BeforeEach
    void setUp() {
        request = new FcmPushRequest("test-device-token-1234", "테스트 제목", "테스트 본문");
    }

    @Test
    @DisplayName("FCM 발송 성공 시 messageId를 포함한 성공 결과를 반환한다")
    void send_success() throws Exception {
        // given
        String expectedMessageId = "projects/test/messages/12345";
        when(firebaseMessaging.send(any(Message.class))).thenReturn(expectedMessageId);

        // when
        FcmPushResult result = fcmPushService.send(request);

        // then
        assertThat(result.success()).isTrue();
        assertThat(result.messageId()).isEqualTo(expectedMessageId);
        assertThat(result.failureReason()).isNull();
        assertThat(result.failureCategory()).isNull();
    }

    @Test
    @DisplayName("data 필드가 포함된 요청도 정상 발송된다")
    void send_withData_success() throws Exception {
        // given
        FcmPushRequest requestWithData = new FcmPushRequest(
                "test-device-token-1234", "제목", "본문",
                Map.of("orderId", "123", "type", "ORDER_UPDATE")
        );
        when(firebaseMessaging.send(any(Message.class))).thenReturn("msg-id");

        // when
        FcmPushResult result = fcmPushService.send(requestWithData);

        // then
        assertThat(result.success()).isTrue();
    }

    @ParameterizedTest(name = "{0} → {1} (category: {2})")
    @MethodSource("fcmErrorCodeMappings")
    @DisplayName("FCM 에러코드별 실패 사유와 카테고리가 정확히 매핑된다")
    void send_failure_errorCodeMapping(
            MessagingErrorCode errorCode,
            FcmFailureReason expectedReason,
            FcmFailureReason.FailureCategory expectedCategory
    ) throws Exception {
        // given
        FirebaseMessagingException exception = createFirebaseMessagingException(errorCode);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(exception);

        // when
        FcmPushResult result = fcmPushService.send(request);

        // then
        assertThat(result.success()).isFalse();
        assertThat(result.messageId()).isNull();
        assertThat(result.failureReason()).isEqualTo(expectedReason);
        assertThat(result.failureCategory()).isEqualTo(expectedCategory);
    }

    static Stream<Arguments> fcmErrorCodeMappings() {
        return Stream.of(
                Arguments.of(
                        MessagingErrorCode.INVALID_ARGUMENT,
                        FcmFailureReason.INVALID_TOKEN,
                        FcmFailureReason.FailureCategory.INVALID_TOKEN
                ),
                Arguments.of(
                        MessagingErrorCode.SENDER_ID_MISMATCH,
                        FcmFailureReason.INVALID_TOKEN,
                        FcmFailureReason.FailureCategory.INVALID_TOKEN
                ),
                Arguments.of(
                        MessagingErrorCode.UNREGISTERED,
                        FcmFailureReason.UNREGISTERED_TOKEN,
                        FcmFailureReason.FailureCategory.REQUIRES_TOKEN_REFRESH
                ),
                Arguments.of(
                        MessagingErrorCode.QUOTA_EXCEEDED,
                        FcmFailureReason.RATE_LIMIT_EXCEEDED,
                        FcmFailureReason.FailureCategory.RETRYABLE
                ),
                Arguments.of(
                        MessagingErrorCode.UNAVAILABLE,
                        FcmFailureReason.SERVER_UNAVAILABLE,
                        FcmFailureReason.FailureCategory.RETRYABLE
                ),
                Arguments.of(
                        MessagingErrorCode.INTERNAL,
                        FcmFailureReason.INTERNAL_ERROR,
                        FcmFailureReason.FailureCategory.NON_RETRYABLE
                ),
                Arguments.of(
                        MessagingErrorCode.THIRD_PARTY_AUTH_ERROR,
                        FcmFailureReason.INTERNAL_ERROR,
                        FcmFailureReason.FailureCategory.NON_RETRYABLE
                )
        );
    }

    @Test
    @DisplayName("UNREGISTERED 에러 시 requiresTokenRefresh()가 true를 반환한다")
    void send_unregistered_requiresTokenRefresh() throws Exception {
        // given
        FirebaseMessagingException exception = createFirebaseMessagingException(MessagingErrorCode.UNREGISTERED);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(exception);

        // when
        FcmPushResult result = fcmPushService.send(request);

        // then
        assertThat(result.requiresTokenRefresh()).isTrue();
        assertThat(result.isRetryable()).isFalse();
    }

    @Test
    @DisplayName("UNAVAILABLE 에러 시 isRetryable()이 true를 반환한다")
    void send_unavailable_isRetryable() throws Exception {
        // given
        FirebaseMessagingException exception = createFirebaseMessagingException(MessagingErrorCode.UNAVAILABLE);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(exception);

        // when
        FcmPushResult result = fcmPushService.send(request);

        // then
        assertThat(result.isRetryable()).isTrue();
        assertThat(result.requiresTokenRefresh()).isFalse();
    }

    @Test
    @DisplayName("INTERNAL 에러 시 재시도 불가하다")
    void send_internal_notRetryable() throws Exception {
        // given
        FirebaseMessagingException exception = createFirebaseMessagingException(MessagingErrorCode.INTERNAL);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(exception);

        // when
        FcmPushResult result = fcmPushService.send(request);

        // then
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.requiresTokenRefresh()).isFalse();
        assertThat(result.failureCategory()).isEqualTo(FcmFailureReason.FailureCategory.NON_RETRYABLE);
    }

    private FirebaseMessagingException createFirebaseMessagingException(MessagingErrorCode errorCode) {
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        when(exception.getMessagingErrorCode()).thenReturn(errorCode);
        when(exception.getMessage()).thenReturn("test error: " + errorCode.name());
        return exception;
    }
}
