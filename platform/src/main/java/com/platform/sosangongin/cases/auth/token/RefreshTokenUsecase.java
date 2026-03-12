package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenUsecase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshTokenResult reissue(RefreshTokenRequest request) {
        String requestToken = request.getRefreshToken();

        // 1. 토큰에서 사용자 ID 추출 (파싱 불가 시 예외 발생)
        UUID userId;
        try {
            userId = jwtService.getUserIdFromToken(requestToken);
        } catch (Exception e) {
            log.warn("Invalid refresh token format or signature: {}", e.getMessage());
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Invalid refresh token")
                    .build();
        }

        // 2. 사용자 존재 여부 확인
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.warn("User not found for ID: {}", userId);
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();

        // 3. 해당 사용자의 최신 리프레시 토큰 조회
        Optional<RefreshToken> latestTokenOptional = refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user);
        if (latestTokenOptional.isEmpty()) {
            log.warn("No refresh token found for user: {}", userId);
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("Refresh token not found")
                    .build();
        }

        RefreshToken latestToken = latestTokenOptional.get();

        // 4. 요청된 토큰과 최신 토큰 값 비교 (Rotation 정책: 일치하지 않으면 탈취 의심)
        if (!latestToken.getTokenValue().equals(requestToken)) {
            log.warn("Refresh token mismatch for user: {}. Possible token theft.", userId);
            // 보안 정책에 따라 모든 토큰을 무효화할 수도 있음
            refreshTokenRepository.deleteAllByUser(user);
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("Invalid refresh token (Reuse detected)")
                    .build();
        }

        // 5. 만료 여부 확인 (DB 상의 expiresAt)
        if (latestToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired for user: {}", userId);
            refreshTokenRepository.delete(latestToken); // 만료된 토큰 정리
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("Refresh token expired")
                    .build();
        }

        // 6. 기존 토큰 삭제 (Rotation)
        refreshTokenRepository.delete(latestToken);

        // 7. 새로운 토큰 발급
        String newAccessToken = jwtService.createToken(user.getId());
        String newRefreshTokenStr = jwtService.createRefreshToken(user.getId());

        // 8. 새로운 리프레시 토큰 저장
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .tokenValue(newRefreshTokenStr)
                .expiresAt(LocalDateTime.now().plus(jwtProperties.getRefreshTokenExpirationTime(), ChronoUnit.MILLIS))
                .build();
        
        refreshTokenRepository.save(newRefreshToken);

        log.info("Tokens reissued for user: {}", user.getId());

        return RefreshTokenResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .build();
    }
}
