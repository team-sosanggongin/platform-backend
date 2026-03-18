package com.platform.sosangongin.services.oauth.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "oauth.kakao.client-id=test-client",
        "oauth.kakao.client-secret=test-secret"
})
class KakaoOauthPropertiesTest {

    @Autowired
    KakaoOauthProperties properties;
    
    @DisplayName("환경변수에서 카카오 관련 정보를 가져오는지 확인 - 추후 민감정보는 환경변수에서 가져와 yml로 변환 후 처리할 것")
    @Test
    void t(){
        assertEquals("test-client", properties.getClientId());
    }


}