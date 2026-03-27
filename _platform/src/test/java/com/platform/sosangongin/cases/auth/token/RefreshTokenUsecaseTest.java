package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.InvalidTokenException;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUsecaseTest {

    @InjectMocks
    private RefreshTokenUsecase refreshTokenUsecase;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private TimeGeneratorService timeGeneratorService;

    private UUID userId;
    private User user;
    private static final String REQUEST_TOKEN = "valid-refresh-token";
    private static final ClientPlatform AGENT_TYPE = ClientPlatform.ANDROID;
    private static final String DEVICE_INFO = "Galaxy S24";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 3, 27, 12, 0);

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).name("테스트유저").phoneNumber("01012345678").build();
    }

    private RefreshTokenRequest createRequest() {
        return RefreshTokenRequest.builder()
                .refreshToken(REQUEST_TOKEN)
                .agentType(AGENT_TYPE)
                .deviceInfo(DEVICE_INFO)
                .build();
    }

    private RefreshToken createRefreshToken(LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .tokenValue(REQUEST_TOKEN)
                .user(user)
                .agentType(AGENT_TYPE)
                .deviceInfo(DEVICE_INFO)
                .expiresAt(expiresAt)
                .build();
    }

    @Test
    @DisplayName("정상 요청 시 accessToken만 발급하고 success=true를 반환한다")
    void reissue_success_accessTokenOnly() {
        // given
        RefreshToken token = createRefreshToken(NOW.plusDays(5));

        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(token));
        given(timeGeneratorService.now()).willReturn(NOW);
        given(jwtService.createToken(any(UUID.class))).willReturn("new-access-token");

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(createRequest());

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isNull();
        assertThat(result.getFailureReason()).isNull();
    }

    @Test
    @DisplayName("refreshToken 만료 임박 시 refreshToken도 함께 재발급한다")
    void reissue_success_withRefreshTokenRenewal() {
        // given
        RefreshToken token = createRefreshToken(NOW.plusHours(5)); // 5시간 남음 (10시간 미만)

        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(token));
        given(timeGeneratorService.now()).willReturn(NOW);
        given(jwtService.createToken(any(UUID.class))).willReturn("new-access-token");
        given(jwtService.createRefreshToken(any(UUID.class))).willReturn("new-refresh-token");

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(createRequest());

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("DB에 refreshToken이 없으면 TOKEN_NOT_FOUND를 반환한다")
    void reissue_fail_tokenNotFound() {
        // given
        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.empty());

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(createRequest());

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(RefreshTokenFailureReason.TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("refreshToken이 만료되었으면 TOKEN_EXPIRED를 반환하고 토큰을 삭제한다")
    void reissue_fail_tokenExpired() {
        // given
        RefreshToken expiredToken = createRefreshToken(NOW.minusHours(1));

        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(expiredToken));
        given(timeGeneratorService.now()).willReturn(NOW);

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(createRequest());

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(RefreshTokenFailureReason.TOKEN_EXPIRED);
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    @DisplayName("기기 정보가 다르면 DEVICE_MISMATCH를 반환한다")
    void reissue_fail_deviceMismatch() {
        // given
        RefreshToken token = RefreshToken.builder()
                .tokenValue(REQUEST_TOKEN)
                .user(user)
                .agentType(ClientPlatform.IOS)
                .deviceInfo("iPhone 15")
                .expiresAt(NOW.plusDays(5))
                .build();

        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(token));
        given(timeGeneratorService.now()).willReturn(NOW);

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(createRequest());

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(RefreshTokenFailureReason.DEVICE_MISMATCH);
    }

    @Test
    @DisplayName("토큰 값이 불일치하면 토큰 재사용으로 간주하여 예외를 던지고 전체 토큰을 삭제한다")
    void reissue_throw_tokenReused() {
        // given
        RefreshToken token = RefreshToken.builder()
                .tokenValue("different-token-value")
                .user(user)
                .agentType(AGENT_TYPE)
                .deviceInfo(DEVICE_INFO)
                .expiresAt(NOW.plusDays(5))
                .build();

        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(token));

        // when & then
        assertThatThrownBy(() -> refreshTokenUsecase.reissue(createRequest()))
                .isInstanceOf(InvalidTokenException.class);
        verify(refreshTokenRepository).deleteAllByUser(user);
    }

    @Test
    @DisplayName("토큰 안의 userId로 유저를 못 찾으면 예외를 던진다")
    void reissue_throw_userNotFound() {
        // given
        given(jwtService.getUserIdFromToken(REQUEST_TOKEN)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenUsecase.reissue(createRequest()))
                .isInstanceOf(InvalidTokenException.class);
    }
}
