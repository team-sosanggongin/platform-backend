package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.platform.sosangongin.domains.user.SocialProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class LoginUsecaseTest {

    @Autowired
    private LoginUsecase loginUsecase;

    @Autowired
    private UserSocialAuthRepository userSocialAuthRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private OauthService mockOauthService;

    @MockBean
    private JwtService mockJwtService;

    @Test
    @DisplayName("최초 가입 시, 유저 정보만 생성되고 토큰 없이 404를 반환한다.")
    void firstSignup_ShouldCreateUserAndReturnNotFound_WithoutTokens() {
        // given
        LoginRequest request = new LoginRequest("mockCode", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "uniqueId", "userName", "010-1234-5678"));

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getAccessToken()).isNull();
        assertThat(result.getRefreshToken()).isNull();
        assertThat(userRepository.findByPhoneNumber("010-1234-5678")).isPresent();
    }

    @Test
    @DisplayName("기존 유저 로그인 시, Access/Refresh 토큰을 발급하고 200 OK를 반환한다.")
    void existingUserLogin_ShouldIssueTokensAndReturnOk() {
        // given
        User user = User.builder()
                .phoneNumber("010-1111-2222")
                .name("Existing User")
                .build();
        userRepository.save(user);

        UserSocialAuth socialAuth = UserSocialAuth.builder()
                .user(user)
                .provider(KAKAO)
                .providerUserId("existing-id")
                .build();
        userSocialAuthRepository.save(socialAuth);

        LoginRequest request = new LoginRequest("code", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "existing-id", "Existing User", "010-1111-2222"));

        when(mockJwtService.createToken(any(UUID.class))).thenReturn("fake-access-token");
        when(mockJwtService.createRefreshToken(any(UUID.class))).thenReturn("fake-refresh-token");

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isEqualTo("fake-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("fake-refresh-token");

        // DB에 RefreshToken이 저장되었는지 확인
        Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findAll().stream().findFirst();
        assertThat(savedRefreshToken).isPresent();
        assertThat(savedRefreshToken.get().getTokenValue()).isEqualTo("fake-refresh-token");
        assertThat(savedRefreshToken.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("다른 소셜 제공자로 신규 로그인 시, 토큰을 발급하고 200 OK를 반환한다.")
    void newUserLoginWithExistingPhoneNumber_ShouldIssueTokensAndReturnOk() {
        // given
        User user = User.builder()
                .phoneNumber("010-3333-4444")
                .name("Existing User")
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest("code", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "new-social-id", "Existing User", "010-3333-4444"));

        when(mockJwtService.createToken(any(UUID.class))).thenReturn("new-access-token");
        when(mockJwtService.createRefreshToken(any(UUID.class))).thenReturn("new-refresh-token");

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");

        // 새로운 소셜 정보가 저장되었는지 확인
        Assertions.assertNotNull(userSocialAuthRepository.findByProviderAndProviderUserId(KAKAO, "new-social-id"));
    }
}
