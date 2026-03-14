package com.platform.sosangongin.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.api.controllers.dto.LoginApiRequest;
import com.platform.sosangongin.api.controllers.dto.PhoneVerificationApiRequest;
import com.platform.sosangongin.api.controllers.dto.RefreshTokenApiRequest;
import com.platform.sosangongin.cases.auth.login.LoginRequest;
import com.platform.sosangongin.cases.auth.login.LoginResult;
import com.platform.sosangongin.cases.auth.login.LoginUsecase;
import com.platform.sosangongin.cases.auth.social.SocialAuthRedirectCase;
import com.platform.sosangongin.cases.auth.social.SocialAuthRequest;
import com.platform.sosangongin.cases.auth.social.SocialAuthResult;
import com.platform.sosangongin.cases.auth.token.RefreshTokenRequest;
import com.platform.sosangongin.cases.auth.token.RefreshTokenResult;
import com.platform.sosangongin.cases.auth.token.RefreshTokenUsecase;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationRequest;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationResult;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({CommonResultResponseAdvice.class, SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginUsecase loginUsecase;

    @MockBean
    private SocialAuthRedirectCase socialAuthRedirectCase;

    @MockBean
    private RefreshTokenUsecase refreshTokenUsecase;

    @MockBean
    private PhoneVerificationUsecase phoneVerificationUsecase;

    @MockBean
    private JwtService jwtService; // SecurityConfig 의존성

    @Test
    @DisplayName("GET /api/v1/auth/social/redirect - 성공")
    void getSocialRedirectUrl() throws Exception {
        // given
        SocialAuthResult expectedResult = SocialAuthResult.builder()
                .httpStatus(HttpStatus.OK)
                .url("https://auth.url")
                .provider("KAKAO")
                .build();
        given(socialAuthRedirectCase.getRedirectionUrl(any(SocialAuthRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(get("/api/v1/auth/social/redirect")
                        .param("provider", "KAKAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://auth.url"))
                .andExpect(jsonPath("$.provider").value("KAKAO"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - 성공")
    void login() throws Exception {
        // given
        LoginApiRequest request = new LoginApiRequest();
        request.setCode("test-code");
        request.setProvider(SocialProvider.KAKAO);
        
        LoginResult expectedResult = LoginResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
        given(loginUsecase.loginAfterSocialEvent(any(LoginRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - 실패 (최초가입, 404)")
    void login_NotFound() throws Exception {
        // given
        LoginApiRequest request = new LoginApiRequest();
        request.setCode("test-code");
        request.setProvider(SocialProvider.KAKAO);
        
        LoginResult expectedResult = LoginResult.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("phone number verification required")
                .build();
        given(loginUsecase.loginAfterSocialEvent(any(LoginRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("phone number verification required"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh - 성공")
    void refresh() throws Exception {
        // given
        RefreshTokenApiRequest request = new RefreshTokenApiRequest();
        request.setRefreshToken("old-token");
        
        RefreshTokenResult expectedResult = RefreshTokenResult.builder()
                .httpStatus(HttpStatus.OK)
                .accessToken("new-access")
                .refreshToken("new-refresh")
                .build();
        given(refreshTokenUsecase.reissue(any(RefreshTokenRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/verify-phone - 성공")
    void verifyPhone() throws Exception {
        // given
        PhoneVerificationApiRequest request = new PhoneVerificationApiRequest();
        request.setPhoneVerificationRequest(false);
        request.setUserId("user-id");
        request.setCode("12345");
        
        PhoneVerificationResult expectedResult = PhoneVerificationResult.builder()
                .httpStatus(HttpStatus.OK)
                .build();
        given(phoneVerificationUsecase.handlePhoneVerification(any(PhoneVerificationRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/verify-phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
