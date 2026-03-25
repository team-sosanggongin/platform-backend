package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.cases.app.CheckAppStatusRequest;
import com.platform.sosangongin.cases.app.CheckAppStatusResult;
import com.platform.sosangongin.cases.app.CheckAppStatusUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppController.class)
@Import({SecurityConfig.class, WebMvcConfig.class, CommonResultResponseAdvice.class})
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckAppStatusUsecase checkAppStatusUsecase;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/v1/app/status - 서비스 정상")
    void checkStatus_serviceAvailable() throws Exception {
        // given
        CheckAppStatusResult result = CheckAppStatusResult.builder()
                .httpStatus(HttpStatus.OK)
                .serviceAvailable(true)
                .maintenance(CheckAppStatusResult.MaintenanceInfo.builder()
                        .active(false)
                        .build())
                .version(CheckAppStatusResult.VersionInfo.builder()
                        .latestVersion("2.0.0")
                        .latestVersionCode(20)
                        .supported(true)
                        .forceUpdateRequired(false)
                        .build())
                .build();

        given(checkAppStatusUsecase.check(any(CheckAppStatusRequest.class))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/app/status")
                        .param("platform", "ANDROID")
                        .param("versionCode", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceAvailable").value(true))
                .andExpect(jsonPath("$.maintenance.active").value(false))
                .andExpect(jsonPath("$.version.latestVersion").value("2.0.0"))
                .andExpect(jsonPath("$.version.supported").value(true))
                .andExpect(jsonPath("$.version.forceUpdateRequired").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/app/status - 점검 중")
    void checkStatus_underMaintenance() throws Exception {
        // given
        CheckAppStatusResult result = CheckAppStatusResult.builder()
                .httpStatus(HttpStatus.OK)
                .serviceAvailable(false)
                .maintenance(CheckAppStatusResult.MaintenanceInfo.builder()
                        .active(true)
                        .reason("서버 업데이트")
                        .startedAt(LocalDateTime.of(2026, 3, 23, 2, 0))
                        .endedAt(LocalDateTime.of(2026, 3, 23, 5, 0))
                        .build())
                .build();

        given(checkAppStatusUsecase.check(any(CheckAppStatusRequest.class))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/app/status")
                        .param("platform", "IOS")
                        .param("versionCode", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceAvailable").value(false))
                .andExpect(jsonPath("$.maintenance.active").value(true))
                .andExpect(jsonPath("$.maintenance.reason").value("서버 업데이트"));
    }

    @Test
    @DisplayName("GET /api/v1/app/status - 강제 업데이트 필요")
    void checkStatus_forceUpdateRequired() throws Exception {
        // given
        CheckAppStatusResult result = CheckAppStatusResult.builder()
                .httpStatus(HttpStatus.OK)
                .serviceAvailable(false)
                .maintenance(CheckAppStatusResult.MaintenanceInfo.builder()
                        .active(false)
                        .build())
                .version(CheckAppStatusResult.VersionInfo.builder()
                        .latestVersion("3.0.0")
                        .latestVersionCode(30)
                        .supported(false)
                        .forceUpdateRequired(true)
                        .build())
                .build();

        given(checkAppStatusUsecase.check(any(CheckAppStatusRequest.class))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/app/status")
                        .param("platform", "ANDROID")
                        .param("versionCode", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceAvailable").value(false))
                .andExpect(jsonPath("$.version.supported").value(false))
                .andExpect(jsonPath("$.version.forceUpdateRequired").value(true));
    }
}
