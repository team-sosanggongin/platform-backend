package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUsecaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenUsecase refreshTokenUsecase;

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId); // ID 설정

        String oldRefreshToken = "old-refresh-token";
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken(oldRefreshToken).build();

        RefreshToken storedToken = RefreshToken.builder()
                .user(user)
                .tokenValue(oldRefreshToken)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        given(jwtService.getUserIdFromToken(oldRefreshToken)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(storedToken));
        given(jwtService.createToken(any(UUID.class))).willReturn("new-access-token");
        given(jwtService.createRefreshToken(any(UUID.class))).willReturn("new-refresh-token");

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenRepository).delete(storedToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("실패: 유효하지 않은 토큰 형식")
    void reissue_Fail_InvalidTokenFormat() {
        // given
        String invalidToken = "not-a-jwt";
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken(invalidToken).build();
        given(jwtService.getUserIdFromToken(invalidToken)).willThrow(new SignatureException(""));

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("Invalid refresh token");
    }

    @Test
    @DisplayName("실패: 토큰 재사용 의심")
    void reissue_Fail_TokenMismatch() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        String requestToken = "used-token"; // 재사용 시도 토큰
        String latestTokenValue = "latest-token"; // DB에 있는 최신 토큰
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken(requestToken).build();

        RefreshToken latestToken = RefreshToken.builder()
                .user(user)
                .tokenValue(latestTokenValue)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        given(jwtService.getUserIdFromToken(requestToken)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(latestToken));

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getMessage()).contains("Reuse detected");
        verify(refreshTokenRepository).deleteAllByUser(user); // 모든 토큰 삭제
    }

    @Test
    @DisplayName("실패: 만료된 토큰")
    void reissue_Fail_ExpiredToken() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        String expiredToken = "expired-token";
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken(expiredToken).build();

        RefreshToken storedToken = RefreshToken.builder()
                .user(user)
                .tokenValue(expiredToken)
                .expiresAt(LocalDateTime.now().minusDays(1)) // 만료됨
                .build();

        given(jwtService.getUserIdFromToken(expiredToken)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)).willReturn(Optional.of(storedToken));

        // when
        RefreshTokenResult result = refreshTokenUsecase.reissue(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getMessage()).contains("Refresh token expired");
        verify(refreshTokenRepository).delete(storedToken);
    }
}
