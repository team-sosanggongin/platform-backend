package com.platform.sosangongin.services.fcm;

import java.util.Map;

public record FcmPushRequest(
        String deviceToken,
        String title,
        String body,
        Map<String, String> data
) {
    public FcmPushRequest(String deviceToken, String title, String body) {
        this(deviceToken, title, body, Map.of());
    }
}
