package com.platform.sosangongin.services.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"stg", "prod"})
@RequiredArgsConstructor
@Slf4j
public class FcmPushServiceImpl implements FcmPushService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public FcmPushResult send(FcmPushRequest request) {
        Message message = buildMessage(request);

        try {
            String messageId = firebaseMessaging.send(message);
            log.debug("FCM 발송 성공: messageId={}, deviceToken={}", messageId, maskToken(request.deviceToken()));
            return FcmPushResult.success(messageId);
        } catch (FirebaseMessagingException e) {
            FcmFailureReason reason = mapErrorCode(e.getMessagingErrorCode());
            log.warn("FCM 발송 실패: reason={}, errorCode={}, deviceToken={}",
                    reason, e.getMessagingErrorCode(), maskToken(request.deviceToken()), e);
            return FcmPushResult.failure(reason);
        }
    }

    private Message buildMessage(FcmPushRequest request) {
        Message.Builder builder = Message.builder()
                .setToken(request.deviceToken())
                .setNotification(Notification.builder()
                        .setTitle(request.title())
                        .setBody(request.body())
                        .build());

        if (request.data() != null && !request.data().isEmpty()) {
            builder.putAllData(request.data());
        }

        return builder.build();
    }

    private FcmFailureReason mapErrorCode(MessagingErrorCode errorCode) {
        if (errorCode == null) {
            return FcmFailureReason.INTERNAL_ERROR;
        }
        return switch (errorCode) {
            case INVALID_ARGUMENT, SENDER_ID_MISMATCH -> FcmFailureReason.INVALID_TOKEN;
            case UNREGISTERED -> FcmFailureReason.UNREGISTERED_TOKEN;
            case QUOTA_EXCEEDED -> FcmFailureReason.RATE_LIMIT_EXCEEDED;
            case UNAVAILABLE -> FcmFailureReason.SERVER_UNAVAILABLE;
            case INTERNAL, THIRD_PARTY_AUTH_ERROR -> FcmFailureReason.INTERNAL_ERROR;
        };
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
