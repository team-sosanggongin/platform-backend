package com.platform.sosangongin.services.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("local")
public class MockFcmPushProvider implements PushProvider {

    @Override
    public void sendToTopic(String topic, String title, String body) {
        log.info("[MOCK PUSH] 로컬 환경이므로 실제 푸시를 발송하지 않습니다.");
        log.info("[MOCK PUSH] Topic: {}, Title: {}, Body: {}", topic, title, body);
    }

    @Override
    public void sendToToken(String token, String title, String body) {
        log.info("[MOCK PUSH] 로컬 환경이므로 실제 푸시를 발송하지 않습니다.");
        log.info("[MOCK PUSH] Token: {}, Title: {}, Body: {}", token, title, body);
    }
}
