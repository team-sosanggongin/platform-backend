package com.platform.sosangongin.services.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"!stg","!prod"})
@Slf4j
public class MockSmsPushService implements SmsPushService{
    @Override
    public void send(String target, String message) {
        log.debug("send {} to {}", message,target);
    }
}
