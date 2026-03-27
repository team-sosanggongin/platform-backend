package com.platform.sosangongin.api.controllers.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.api.controllers.dto.RefreshTokenApiRequest;
import com.platform.sosangongin.cases.auth.token.RefreshTokenFailureReason;
import com.platform.sosangongin.cases.auth.token.RefreshTokenResult;
import com.platform.sosangongin.cases.auth.token.RefreshTokenUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TokenController.class)
@Import({SecurityConfig.class, WebMvcConfig.class})
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefreshTokenUsecase refreshTokenUsecase;

    @MockBean
    private JwtService jwtService;

    private RefreshTokenApiRequest createRequest() {
        RefreshTokenApiRequest request = new RefreshTokenApiRequest();
        request.setRefreshToken("valid-refresh-token");
        request.setAgentType(ClientPlatform.ANDROID);
        request.setDeviceInfo("Galaxy S24");
        return request;
    }

    @Test
    @DisplayName("POST /api/v1/token/refresh - 재발급 성공, accessToken만 반환")
    void refresh_success_accessTokenOnly() throws Exception {
        // given
        RefreshTokenResult result = RefreshTokenResult.builder()
                .success(true)
                .accessToken("new-access-token")
                .build();

        given(refreshTokenUsecase.reissue(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").isEmpty())
                .andExpect(jsonPath("$.failureReason").isEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/token/refresh - 재발급 성공, refreshToken도 함께 갱신")
    void refresh_success_withRefreshToken() throws Exception {
        // given
        RefreshTokenResult result = RefreshTokenResult.builder()
                .success(true)
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        given(refreshTokenUsecase.reissue(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/v1/token/refresh - 토큰 만료")
    void refresh_fail_tokenExpired() throws Exception {
        // given
        RefreshTokenResult result = RefreshTokenResult.builder()
                .failureReason(RefreshTokenFailureReason.TOKEN_EXPIRED)
                .build();

        given(refreshTokenUsecase.reissue(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.failureReason").value("TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.accessToken").isEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/token/refresh - 기기 불일치")
    void refresh_fail_deviceMismatch() throws Exception {
        // given
        RefreshTokenResult result = RefreshTokenResult.builder()
                .failureReason(RefreshTokenFailureReason.DEVICE_MISMATCH)
                .build();

        given(refreshTokenUsecase.reissue(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.failureReason").value("DEVICE_MISMATCH"));
    }

    @Test
    @DisplayName("POST /api/v1/token/refresh - 토큰 없음")
    void refresh_fail_tokenNotFound() throws Exception {
        // given
        RefreshTokenResult result = RefreshTokenResult.builder()
                .failureReason(RefreshTokenFailureReason.TOKEN_NOT_FOUND)
                .build();

        given(refreshTokenUsecase.reissue(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.failureReason").value("TOKEN_NOT_FOUND"));
    }
}
