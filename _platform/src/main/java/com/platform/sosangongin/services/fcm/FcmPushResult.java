package com.platform.sosangongin.services.fcm;

public record FcmPushResult(
        boolean success,
        String messageId,
        FcmFailureReason failureReason
) {
    public static FcmPushResult success(String messageId) {
        return new FcmPushResult(true, messageId, null);
    }

    public static FcmPushResult failure(FcmFailureReason reason) {
        return new FcmPushResult(false, null, reason);
    }

    public FcmFailureReason.FailureCategory failureCategory() {
        return failureReason != null ? failureReason.getCategory() : null;
    }

    public boolean isRetryable() {
        return failureReason != null
                && failureReason.getCategory() == FcmFailureReason.FailureCategory.RETRYABLE;
    }

    public boolean requiresTokenRefresh() {
        return failureReason != null
                && failureReason.getCategory() == FcmFailureReason.FailureCategory.REQUIRES_TOKEN_REFRESH;
    }
}
