package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.InvalidTokenException;
import com.platform.sosangongin.errors.InvalidTokenUsage;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
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
    private final TimeGeneratorService timeGeneratorService;

    @Transactional
    public RefreshTokenResult reissue(RefreshTokenRequest request) throws InvalidTokenException {
        String requestToken = request.getRefreshToken();

        UUID userId = this.jwtService.getUserIdFromToken(requestToken);

        User user = this.userRepository.findById(userId).orElseThrow(()->new InvalidTokenException("user not found", requestToken, InvalidTokenUsage.INVALID_CLAIMS));

        RefreshToken latestRefreshToken = this.refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user)
                .orElseThrow(()->new InvalidTokenException("No refresh token found", userId, requestToken, InvalidTokenUsage.INVALID_STATE));

        if(!latestRefreshToken.isTokenValueEquals(requestToken)){
            this.refreshTokenRepository.deleteAllByUser(user);
            throw new InvalidTokenException("received request token is not the latest one, this could mean refresh token is reused", userId, requestToken, InvalidTokenUsage.INVALID_STATE);
        }

        if (latestRefreshToken.isBefore(this.timeGeneratorService.now())) {
            log.warn("Refresh token expired for user: {}", userId);
            this.refreshTokenRepository.delete(latestRefreshToken); // 만료된 토큰 정리
            return RefreshTokenResult.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("Refresh token expired")
                    .build();
        }

        this.refreshTokenRepository.delete(latestRefreshToken);

        String newAccessToken = jwtService.createToken(user.getId(), request.getUserAgentDto());
        String newRefreshTokenStr = jwtService.createRefreshToken(user.getId(), request.getUserAgentDto());

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .tokenValue(newRefreshTokenStr)
                .expiresAt(this.timeGeneratorService.now().plus(jwtProperties.getRefreshTokenExpirationTime(), ChronoUnit.MILLIS))
                .build();
        
        this.refreshTokenRepository.save(newRefreshToken);

        log.debug("Tokens reissued for user: {}", user.getId());

        return RefreshTokenResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .build();
    }
}
