package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.token.RefreshToken;
import com.platform.sosangongin.domains.token.RefreshTokenRepository;
import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.jwt.JwtProperties;
import com.platform.sosangongin.services.jwt.JwtService;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

    @Transactional
    public LoginResult loginAfterSocialEvent(LoginRequest loginRequest) {
        AuthResponse authRes = this.oauthService.getAuth(loginRequest.getProvider(), loginRequest.getCode());
        UserSocialAuth authData = this.userSocialAuthRepository.findByProviderAndProviderUserId(authRes.provider(), authRes.uniqueId());

        if (authData == null) {
            log.info("this user {} is not existing", authRes.uniqueIdWithProvider());
            // 다른 소셜 로그인으로 접근한 기록이 있는지 확인 필요
            Optional<User> userOptional = this.userRepository.findByPhoneNumber(authRes.phoneNumber());
            if (userOptional.isEmpty()) {
                log.info("this user {} is not present in the db", authRes.uniqueIdWithProvider());
                // 최초 로그인이기 때문에 유저 회원가입 프로세스로 유도해야 함.

                User newUser = User.builder()
                        .name(authRes.userName())
                        .phoneNumber(authRes.phoneNumber())
                        .build();

                this.userRepository.save(newUser);

                UserSocialAuth providerAuthHistory = UserSocialAuth.builder()
                        .user(newUser)
                        .provider(authRes.provider())
                        .providerUserId(authRes.uniqueId())
                        .build();

                this.userSocialAuthRepository.save(providerAuthHistory);

                return LoginResult.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message("phone number verification required")
                        .build();
            } else {
                log.info("this user {} is existing", authRes.uniqueIdWithProvider());
                // 이미 존재하는 고객이나, 해당 provider로는 최초로 접근한 경우
                User existingUser = userOptional.get();
                UserSocialAuth providerAuthHistory = UserSocialAuth.builder()
                        .user(existingUser)
                        .provider(authRes.provider())
                        .providerUserId(authRes.uniqueId())
                        .build();

                this.userSocialAuthRepository.save(providerAuthHistory);

                return createTokensAndReturn(existingUser);
            }

        }

        log.info("this user already is registered with the system");
        User user = authData.getUser();

        return createTokensAndReturn(user);
    }

    private LoginResult createTokensAndReturn(User user) {
        // 기존 Refresh Token 삭제 (단일 로그인 정책 등 필요에 따라 조정 가능)
        // TODO :: 토큰이 있는 상태에서 로그인하는 것에 대한 정책 정의
        this.refreshTokenRepository.deleteAllByUser(user);

        // Access Token 발급
        String accessToken = this.jwtService.createToken(user.getId());

        // Refresh Token 발급
        String refreshTokenStr = this.jwtService.createRefreshToken(user.getId());

        // Refresh Token DB 저장
        // TODO: UserAgent 등 추가 정보 수집 가능 시 매핑
        RefreshToken refreshToken = new RefreshToken(
                user,
                refreshTokenStr,
                null, // UserAgent
                LocalDateTime.now().plus(this.jwtProperties.getRefreshTokenExpirationTime(), ChronoUnit.MILLIS)
        );
        this.refreshTokenRepository.save(refreshToken);

        return LoginResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }
}
