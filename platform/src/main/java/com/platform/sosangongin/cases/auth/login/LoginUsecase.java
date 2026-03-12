package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class LoginUsecase {

    private final OauthService oauthService;
    private final UserSocialAuthRepository userSocialAuthRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoginResult loginAfterSocialEvent(LoginRequest loginRequest) {
        AuthResponse authRes = this.oauthService.getAuth(loginRequest.getProvider(), loginRequest.getCode());
        UserSocialAuth authData = this.userSocialAuthRepository.findByProviderAndProviderId(authRes.provider(), authRes.uniqueId());

        if (authData == null) {
            log.info("this user {} is not existing", authRes.uniqueIdWithProvider());
            // 다른 소셜 로그인으로 접근한 기록이 있는지 확인 필요
            Optional<User> userOptional = this.userRepository.findByPhoneNumber(authRes.phoneNumber());
            if (userOptional.isEmpty()) {
                log.info("this user {} is not present in the db", authRes.uniqueIdWithProvider());
                // 최초 로그인이기 때문에 유저 회원가입 프로세스로 유도해야 함.
                return LoginResult.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build();
            } else {
                log.info("this user {} is existing", authRes.uniqueIdWithProvider());
                // 이미 존재하는 고객이나, 해당 provider로는 최초로 접근한 경우
                UserSocialAuth providerAuthHistory = UserSocialAuth.builder()
                        .providerId(authRes.uniqueId())
                        .provider(authRes.provider())
                        .user(userOptional.get())
                        .build();

                this.userSocialAuthRepository.save(providerAuthHistory);
                return LoginResult.builder()
                        .httpStatus(HttpStatus.OK)
                        .build();
            }

        }

        log.info("this user already is registered with the system");
        return LoginResult.builder()
                .httpStatus(HttpStatus.OK)
                .build();

    }
}
