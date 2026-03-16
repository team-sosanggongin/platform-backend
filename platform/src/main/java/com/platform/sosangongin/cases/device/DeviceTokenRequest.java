package com.platform.sosangongin.cases.device;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 앱에서 전달하는 토큰 정보를 담는 DTO
 */

@Getter
@NoArgsConstructor
public class DeviceTokenRequest {
    private Long userId;  //현재 비-로그인 시 null 허용
    private String token;   // FCM 디바이스 토큰
    private String deviceType;  // "ANDROID" or "iOS"
}
