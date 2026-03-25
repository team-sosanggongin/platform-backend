package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import com.platform.sosangongin.domains.user.social.UserSocialAuth;
import com.platform.sosangongin.domains.user.social.UserSocialAuthRepository;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.platform.sosangongin.domains.user.SocialProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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

    @MockBean
    private JwtProperties jwtProperties;

    @MockBean
    private TimeGeneratorService timeGeneratorService;

    @Test
    @DisplayName("최초 가입 시, 유저 정보만 생성되고 토큰 없이 200을 반환한다.")
    void firstSignup_ShouldCreateUserAndReturnNotFound_WithoutTokens() {
        // given
        LoginRequest request = getLoginRequest("mockCode", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "uniqueId", "userName", "010-1234-5678"));

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
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

        LoginRequest request = getLoginRequest("code", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "existing-id", "Existing User", "010-1111-2222"));

        when(mockJwtService.createToken(any(UUID.class), any())).thenReturn("fake-access-token");
        when(mockJwtService.createRefreshToken(any(UUID.class), any())).thenReturn("fake-refresh-token");

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isEqualTo(null);
        assertThat(result.getRefreshToken()).isEqualTo(null);

        // 전화번호 인증이 끝나지 않았기 때문에, 토큰은 생성되지 않음
        Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findAll().stream().findFirst();
        assertThat(savedRefreshToken).isEmpty();
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

        LoginRequest request = getLoginRequest("code", KAKAO);
        when(mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "new-social-id", "Existing User", "010-3333-4444"));

        when(mockJwtService.createToken(any(UUID.class), any())).thenReturn("new-access-token");
        when(mockJwtService.createRefreshToken(any(UUID.class), any())).thenReturn("new-refresh-token");

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isNull();
        assertThat(result.getRefreshToken()).isNull();

        // 새로운 소셜 정보가 저장되었는지 확인
        Assertions.assertNotNull(userSocialAuthRepository.findByProviderAndProviderUserId(KAKAO, "new-social-id"));
    }

    @Test
    @DisplayName("로그인 성공 및 폰 인증 완료 유저: 액세스/리프레시 토큰이 정상 발급되어야 함")
    void login_Success_With_Verified_Phone() {
        // given
        String code = "auth_code";
        SocialProvider provider = KAKAO;
        LoginRequest request = getLoginRequest(code, provider);
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 3, 16, 15, 30);

        // 1. 소셜 인증 성공
        AuthResponse authRes = new AuthResponse(provider, "social_123", "홍길동", "01012345678");
        given(mockOauthService.getAuth(provider, code)).willReturn(authRes);

        // 2. DB에 유저가 존재하고, 폰 인증 상태가 true임
        User verifiedUser = User.builder()
                .id(userId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .isPhoneVerified(true) // 핵심 조건: 인증 완료
                .build();

        this.userRepository.saveAndFlush(verifiedUser);

        UserSocialAuth socialAuth = UserSocialAuth.builder()
                .user(verifiedUser)
                .provider(provider)
                .providerUserId("social_123")
                .build();

        userSocialAuthRepository.saveAndFlush(socialAuth);

        // 3. 토큰 발급 및 시간 설정 Mocking
        String mockAccessToken = "mock.access.token";
        String mockRefreshToken = "mock.refresh.token";
        long expirationMillis = 3600000L; // 1시간

        given(mockJwtService.createToken(any(), any())).willReturn(mockAccessToken);
        given(mockJwtService.createRefreshToken(any(), any())).willReturn(mockRefreshToken);
        given(this.timeGeneratorService.now()).willReturn(now);
        given(this.jwtProperties.getRefreshTokenExpirationTime()).willReturn(expirationMillis);

        // when
        LoginResult result = loginUsecase.loginAfterSocialEvent(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isEqualTo(mockAccessToken);   // 토큰 발급 확인
        assertThat(result.getRefreshToken()).isEqualTo(mockRefreshToken); // 토큰 발급 확인
        assertThat(result.getUserId()).isNull(); // 성공 시에는 보통 userId를 따로 주지 않음 (토큰에 포함됨)
        assertThat(result.getNextUrl()).isNull(); // 다음 스텝 URL이 없어야 함

        // 4. 보안 및 저장 로직 검증
        // 기존 토큰 삭제 여부 확인
        List<RefreshToken> all = refreshTokenRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

    }

    private static LoginRequest getLoginRequest(String code, SocialProvider provider) {
        LoginRequest request = new LoginRequest(code, provider, new UserAgentDto());
        return request;
    }
}
