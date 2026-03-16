package com.platform.sosangongin.services.push;

public interface PushProvider {
    void sendToTopic(String topic, String title, String body);
    void sendToToken(String token, String title, String body);
}
