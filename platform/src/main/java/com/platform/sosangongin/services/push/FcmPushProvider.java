package com.platform.sosangongin.services.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!local")
public class FcmPushProvider implements PushProvider {
    @Override
    public void sendToTopic(String topic, String title, String body) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("{}로의 메시지 전송이 성공하였습니다. {}", topic, response);
        } catch (FirebaseMessagingException e) {
            log.error("{}로의 메시지 전송에 실패했습니다.", topic, e);
        }
    }

    @Override
    public void sendToToken(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message to token: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message to token", e);
        }
    }
}
