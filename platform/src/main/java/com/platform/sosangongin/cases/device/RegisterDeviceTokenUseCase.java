package com.platform.sosangongin.cases.device;

import com.platform.sosangongin.domains.device.DeviceToken;
import com.platform.sosangongin.domains.device.DeviceTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterDeviceTokenUseCase {
    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void registerOrUpdateToken(DeviceTokenRequest request) {     
        deviceTokenRepository.findByTokens(request.getToken())     
                .ifPresentOrElse(existingDeviceToken -> {
                    existingDeviceToken.updateUserId(request.getUserId());
                    log.info("{}의 디바이스 토큰이 업데이트 되었습니다.", request.getToken());
                },
                        () -> {
                    DeviceToken newToken = DeviceToken.builder()        
                            .userId(request.getUserId())
                            .tokens(request.getToken())
                            .deviceType(request.getDeviceType())        
                            .build();
                    deviceTokenRepository.save(newToken);
                    log.info("새 디바이스 토큰이 등록되었습니다.");     
                        }
                );
    }
}

