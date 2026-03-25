package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OauthDataFactory {

    public static AuthResponse parse(SocialProvider provider, Map<String, Object> userInfo) {
        return switch (provider) {
            case KAKAO -> parseKakao(userInfo);
        };
    }

    private static AuthResponse parseKakao(Map<String, Object> userInfo) {
        String uniqueId = String.valueOf(userInfo.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        if (kakaoAccount == null) {
            throw new IllegalStateException("카카오 계정 정보를 가져올 수 없습니다.");
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = profile != null ? (String) profile.get("nickname") : null;

        // 카카오는 "+82 10-1234-5678" 형식으로 반환 → "01012345678" 정규화
        String rawPhone = (String) kakaoAccount.get("phone_number");
        String phoneNumber = normalizeKakaoPhoneNumber(rawPhone);

        return new AuthResponse(SocialProvider.KAKAO, uniqueId, nickname, phoneNumber);
    }

    private static String normalizeKakaoPhoneNumber(String rawPhone) {
        if (rawPhone == null) return null;
        // "+82 10-1234-5678" → "01012345678"
        return rawPhone
                .replace("+82 ", "0")
                .replaceAll("[^0-9]", "");
    }
}
