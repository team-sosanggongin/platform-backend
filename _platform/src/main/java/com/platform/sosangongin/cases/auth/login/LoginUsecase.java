package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.domains.user.agents.UserAgent;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import com.platform.sosangongin.domains.user.social.UserSocialAuth;
import com.platform.sosangongin.domains.user.social.UserSocialAuthRepository;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class LoginUsecase {

    private final OauthService oauthService;
    private final UserSocialAuthRepository userSocialAuthRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final TimeGeneratorService timeGeneratorService;

    @Transactional
    public LoginResult loginAfterSocialEvent(LoginRequest loginRequest) {
        AuthResponse authRes = oauthService.getAuth(loginRequest.getProvider(), loginRequest.getCode());

        // 1. 소셜 연동 정보 확인
        Optional<UserSocialAuth> socialAuthOpt = Optional.ofNullable(
                userSocialAuthRepository.findByProviderAndProviderUserId(authRes.provider(), authRes.uniqueId())
        );

        if (socialAuthOpt.isPresent()) {
            User user = socialAuthOpt.get().getUser();
            log.debug("Registered social user login attempt: {}", user.getId());

            return user.isPhoneVerified()
                    ? createTokensAndReturn(user, loginRequest.getUserAgentDto())
                    : redirectForVerification(user.getId(), "Phone verification is needed");
        }

        // 2. 계정 연동 혹은 신규 가입
        return userRepository.findByPhoneNumber(authRes.phoneNumber())
                .map(existingUser -> handleAccountLinking(existingUser, loginRequest.getUserAgentDto(), authRes))
                .orElseGet(() -> handleNewUserRegistration(authRes));
    }

    private LoginResult handleAccountLinking(User user, UserAgentDto userAgentDto, AuthResponse authRes) {
        log.info("Linking existing user {} with new provider {}", user.getId(), authRes.provider());
        saveSocialAuth(user, authRes);

        // 연동 후에도 번호 인증 여부에 따라 토큰 발급 결정
        return user.isPhoneVerified()
                ? createTokensAndReturn(user, userAgentDto)
                : redirectForVerification(user.getId(), "Account linked, but verification needed");
    }

    private LoginResult handleNewUserRegistration(AuthResponse authRes) {
        log.info("Creating new user for: {}", authRes.uniqueIdWithProvider());

        User newUser = userRepository.save(User.builder()
                .name(authRes.userName())
                .phoneNumber(authRes.phoneNumber())
                .isPhoneVerified(false)
                .build());

        saveSocialAuth(newUser, authRes);

        return redirectForVerification(newUser.getId(), "Phone number verification required for new user");
    }

    /**
     * 공통 리다이렉션 응답 생성 (토큰 미발급)
     */
    private LoginResult redirectForVerification(UUID userId, String message) {
        return LoginResult.builder()
                .httpStatus(HttpStatus.OK)
                .message(message)
                .nextUrl("PHONE_VERIFICATION") // 클라이언트용 힌트
                .userId(userId)
                .accessToken(null)
                .refreshToken(null)
                .build();
    }

    private void saveSocialAuth(User user, AuthResponse authRes) {
        UserSocialAuth auth = UserSocialAuth.builder()
                .user(user)
                .provider(authRes.provider())
                .providerUserId(authRes.uniqueId())
                .build();
        userSocialAuthRepository.save(auth);
    }

    private LoginResult createTokensAndReturn(User user, UserAgentDto userAgentDto) {
        refreshTokenRepository.deleteAllByUser(user);

        String accessToken = jwtService.createToken(user.getId(), userAgentDto);
        String refreshTokenStr = jwtService.createRefreshToken(user.getId(), userAgentDto);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenValue(refreshTokenStr)
                .expiresAt(timeGeneratorService.now().plus(jwtProperties.getRefreshTokenExpirationTime(), ChronoUnit.MILLIS))
                .build();

        refreshTokenRepository.save(refreshToken);

        return LoginResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }
}