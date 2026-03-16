package com.platform.sosangongin.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * resources/firebase/serviceAccountKey.json 파일 필요
 * 실제 파일 경로나 환경변수에 맞게 수정 필요
 */

@Slf4j
@Configuration
@Profile("!local") // 로컬 환경에서는 Firebase 설정을 로드하지 않음 (환경 격리)
public class FcmConfig {
    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("firebase/serviceAccountKey.json").getInputStream()))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("Firebase application 초기화 되었습니다.");
            }
        } catch (IOException e) {
            log.error("Firebase initialization failed", e);
        }
    }
}


