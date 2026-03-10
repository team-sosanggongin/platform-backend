package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.oauth.AuthResponse;
import com.platform.sosangongin.services.oauth.OauthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.platform.sosangongin.domains.user.SocialProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class LoginUsecaseTest {

    @Autowired
    LoginUsecase loginUsecase;

    @Autowired
    UserSocialAuthRepository userSocialAuthRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    OauthService mockOauthService;

    @Test
    @DisplayName("최초 로그인 시, 유저 정보가 존재하지 않을 경우, 404를 반환한다.")
    void t(){

        LoginRequest request = LoginRequest.builder()
                .code("mockCode")
                .provider(KAKAO)
                .build();

        when(this.mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "uniqueId", "userName","0100000000"));

        LoginResult loginResult = this.loginUsecase.loginAfterSocialEvent(request);
        assertEquals(HttpStatus.NOT_FOUND,loginResult.getHttpStatus());

    }

    @Test
    @DisplayName("최초 로그인이 아닌 경우, OK를 반환한다.")
    void t2(){

        User user = User.builder()
                .phoneNumber("01000000000")
                .isPhoneVerified(true)
                .name("user")
                .build();

        this.userRepository.saveAndFlush(user);

        UserSocialAuth providerAuthHistory = UserSocialAuth.builder()
                .providerId("uniqueId")
                .provider(KAKAO)
                .user(user)
                .build();

        this.userSocialAuthRepository.saveAndFlush(providerAuthHistory);

        when(this.mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, "uniqueId", "user","01000000000"));

        LoginRequest loginRequest = LoginRequest.builder()
                .code("code")
                .provider(KAKAO)
                .build();

        LoginResult result = this.loginUsecase.loginAfterSocialEvent(loginRequest);

        assertEquals(HttpStatus.OK, result.getHttpStatus());

    }

    @Test
    @DisplayName("소셜 로그인 방식이 최초인 경우, 소셜 로그인 기록을 추가한 이후, OK를 반환한다")
    void t3(){

        String userNumber = "01000000000";
        String userName = "userName";
        String uniqueId = "uniqueId";

        User user = User.builder()
                .phoneNumber(userNumber)
                .isPhoneVerified(true)
                .name(userName)
                .build();

        this.userRepository.saveAndFlush(user);

        when(this.mockOauthService.getAuth(eq(KAKAO), anyString()))
                .thenReturn(new AuthResponse(KAKAO, uniqueId, userName, userNumber));

        LoginRequest loginRequest = LoginRequest.builder()
                .code("code")
                .provider(KAKAO)
                .build();

        LoginResult result = this.loginUsecase.loginAfterSocialEvent(loginRequest);

        assertEquals(HttpStatus.OK, result.getHttpStatus());
        List<UserSocialAuth> authList = this.userSocialAuthRepository.findByUser(user);

        assertEquals(1, authList.size());
        UserSocialAuth auth = authList.get(0);

        assertEquals(uniqueId, auth.getProviderId());
        assertEquals(KAKAO, auth.getProvider());

    }


}