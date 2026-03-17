package com.platform.sosangongin.cases.auth.social;

import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.services.oauth.OauthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialAuthRedirectCaseTest {

    @Mock
    private OauthService oauthService;

    @InjectMocks
    private SocialAuthRedirectCase socialAuthRedirectCase;

    @Test
    @DisplayName("소셜 로그인 리다이렉트 URL 생성 성공")
    void getRedirectionUrlSuccess() {
        // given
        String expectedUrl = "https://kauth.kakao.com/oauth/authorize?client_id=...";
        SocialAuthRequest request = new SocialAuthRequest(SocialProvider.KAKAO);
        given(oauthService.buildAuthorizeUrl(SocialProvider.KAKAO)).willReturn(expectedUrl);

        // when
        SocialAuthResult result = socialAuthRedirectCase.getRedirectionUrl(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getUrl()).isEqualTo(expectedUrl);
        assertThat(result.getProvider()).isEqualTo(SocialProvider.KAKAO.name());
        verify(oauthService, times(1)).buildAuthorizeUrl(SocialProvider.KAKAO);
    }

    @Test
    @DisplayName("지원하지 않는 소셜 로그인 제공자 요청 시 실패 처리")
    void getRedirectionUrlFailWhenProviderIsInvalid() {
        // given
        // OauthService가 IllegalArgumentException을 던지는 상황을 가정 (실제로는 Enum 타입이라 컴파일 단계에서 걸러지지만, 로직 상 예외 처리를 테스트)
        SocialAuthRequest request = new SocialAuthRequest(SocialProvider.KAKAO);
        given(oauthService.buildAuthorizeUrl(any())).willThrow(new IllegalArgumentException("Unsupported provider"));

        // when
        SocialAuthResult result = socialAuthRedirectCase.getRedirectionUrl(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getUrl()).isNull();
        verify(oauthService, times(1)).buildAuthorizeUrl(any());
    }
}
