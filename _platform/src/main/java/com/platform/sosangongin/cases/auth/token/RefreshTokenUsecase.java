package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.InvalidTokenException;
import com.platform.sosangongin.errors.InvalidTokenUsage;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenUsecase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TimeGeneratorService timeGeneratorService;

    @Transactional
    public RefreshTokenResult reissue(RefreshTokenRequest request) {
        String requestToken = request.getRefreshToken();
        UUID userId = jwtService.getUserIdFromToken(requestToken);
        User user = findUserOrThrow(userId, requestToken);
        RefreshToken latestRefreshToken = findLatestRefreshToken(user);

        if (latestRefreshToken == null) {
            return failResult(RefreshTokenFailureReason.TOKEN_NOT_FOUND);
        }

        validateTokenReuse(latestRefreshToken, requestToken, userId, user);

        if (latestRefreshToken.isBefore(timeGeneratorService.now())) {
            refreshTokenRepository.delete(latestRefreshToken);
            return failResult(RefreshTokenFailureReason.TOKEN_EXPIRED);
        }

        if (!latestRefreshToken.isDeviceMatch(request.getAgentType(), request.getDeviceInfo())) {
            return failResult(RefreshTokenFailureReason.DEVICE_MISMATCH);
        }

        return issueTokens(user, latestRefreshToken, request.getAgentType(), request.getDeviceInfo());
    }

    private User findUserOrThrow(UUID userId, String requestToken) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("user not found", requestToken, InvalidTokenUsage.INVALID_CLAIMS));
    }

    private RefreshToken findLatestRefreshToken(User user) {
        return refreshTokenRepository.findTopByUserOrderByExpiresAtDesc(user).orElse(null);
    }

    private void validateTokenReuse(RefreshToken latestRefreshToken, String requestToken, UUID userId, User user) throws InvalidTokenException {
        if (!latestRefreshToken.isTokenValueEquals(requestToken)) {
            refreshTokenRepository.deleteAllByUser(user);
            throw new InvalidTokenException("refresh token reused", userId, requestToken, InvalidTokenUsage.INVALID_STATE);
        }
    }

    private RefreshTokenResult issueTokens(User user, RefreshToken currentToken, ClientPlatform agentType, String deviceInfo) {
        String newAccessToken = jwtService.createToken(user.getId());
        String newRefreshToken = null;

        if (currentToken.shouldRefreshTokenRefreshed(timeGeneratorService.now())) {
            newRefreshToken = jwtService.createRefreshToken(user.getId());
            refreshTokenRepository.save(new RefreshToken(newRefreshToken, user, agentType, deviceInfo, timeGeneratorService.now()));
        }

        log.debug("Access token reissued for user: {}", user.getId());

        return RefreshTokenResult.builder()
                .success(true)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private RefreshTokenResult failResult(RefreshTokenFailureReason reason) {
        return RefreshTokenResult.builder()
                .failureReason(reason)
                .build();
    }
}
