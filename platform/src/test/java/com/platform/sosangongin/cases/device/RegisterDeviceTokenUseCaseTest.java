package com.platform.sosangongin.cases.device;

import com.platform.sosangongin.domains.device.DeviceToken;
import com.platform.sosangongin.domains.device.DeviceTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterDeviceTokenUseCaseTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @InjectMocks
    private RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @Test
    @DisplayName("새로운 토큰이면 DB에 새로 저장(Insert)한다")
    void registerToken_WhenNewToken_ShouldSave() {
        // given
        DeviceTokenRequest request = createRequest(1L, "new_token", "ANDROID");
        given(deviceTokenRepository.findByDeviceToken("new_token")).willReturn(Optional.empty());

        // when
        registerDeviceTokenUseCase.registerOrUpdateToken(request);

        // then
        verify(deviceTokenRepository).save(any(DeviceToken.class)); // save가 호출되어야 함
    }

    @Test
    @DisplayName("이미 존재하는 토큰이면 유저 ID만 업데이트(Update)한다")
    void registerToken_WhenExistingToken_ShouldUpdateUserId() {
        // given
        DeviceTokenRequest request = createRequest(2L, "existing_token", "IOS");
        DeviceToken existingToken = DeviceToken.builder()
                .userId(1L) // 기존에는 다른 유저(1L)의 토큰이었음
                .tokens("existing_token")
                .deviceType("IOS")
                .build();
        
        given(deviceTokenRepository.findByDeviceToken("existing_token")).willReturn(Optional.of(existingToken));

        // when
        registerDeviceTokenUseCase.registerOrUpdateToken(request);

        // then
        verify(deviceTokenRepository, never()).save(any(DeviceToken.class)); // save는 호출되지 않아야 함 (더티 체킹)
        // 기존 토큰의 유저 ID가 새로운 요청의 유저 ID(2L)로 변경되었는지 검증
        assert(existingToken.getUserId().equals(2L));
    }

    // 헬퍼 메서드: 리플렉션을 사용하여 DTO 강제 생성
    private DeviceTokenRequest createRequest(Long userId, String token, String deviceType) {
        DeviceTokenRequest request = new DeviceTokenRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "token", token);
        ReflectionTestUtils.setField(request, "deviceType", deviceType);
        return request;
    }
}
