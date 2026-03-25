package com.platform.sosangongin.services.fcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile({"!stg", "!prod"})
@Slf4j
public class MockFcmPushService implements FcmPushService {

    @Override
    public FcmPushResult send(FcmPushRequest request) {
        log.debug("Mock FCM 발송: deviceToken={}, title={}, body={}",
                request.deviceToken(), request.title(), request.body());
        return FcmPushResult.success("mock-" + UUID.randomUUID());
    }
}
