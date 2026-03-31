package com.platform.sosangongin.api.controllers.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.cases.app.status.CheckAppStatusResult;
import com.platform.sosangongin.cases.app.status.CheckAppStatusUsecase;
import com.platform.sosangongin.cases.app.version.CheckAppVersionResult;
import com.platform.sosangongin.cases.app.version.CheckAppVersionUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppVersionController.class)
@Import({SecurityConfig.class, WebMvcConfig.class})
class AppVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CheckAppVersionUsecase checkAppVersionUsecase;

    @MockBean
    private CheckAppStatusUsecase checkAppStatusUsecase;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/app/version-check - 호환 가능한 버전")
    void checkVersion_compatible() throws Exception {
        // given
        CheckAppVersionApiRequest request = new CheckAppVersionApiRequest();
        request.setPlatform(com.platform.sosangongin.domains.common.ClientPlatform.ANDROID);
        request.setAppVersion("2.0.0");

        CheckAppVersionResult result = CheckAppVersionResult.builder()
                .compatible(true)
                .forceUpdateRequired(false)
                .latestVersion("2.0.0")
                .reason("")
                .build();

        given(checkAppVersionUsecase.check(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/app/version-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatible").value(true))
                .andExpect(jsonPath("$.forceUpdateRequired").value(false))
                .andExpect(jsonPath("$.latestVersion").value("2.0.0"));
    }

    @Test
    @DisplayName("POST /api/v1/app/version-check - 비호환 버전, 강제 업데이트 필요")
    void checkVersion_incompatible_forceUpdate() throws Exception {
        // given
        CheckAppVersionApiRequest request = new CheckAppVersionApiRequest();
        request.setPlatform(com.platform.sosangongin.domains.common.ClientPlatform.ANDROID);
        request.setAppVersion("1.0.0");

        CheckAppVersionResult result = CheckAppVersionResult.builder()
                .compatible(false)
                .forceUpdateRequired(true)
                .latestVersion("3.0.0")
                .reason("current version is not supported.")
                .build();

        given(checkAppVersionUsecase.check(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/app/version-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatible").value(false))
                .andExpect(jsonPath("$.forceUpdateRequired").value(true))
                .andExpect(jsonPath("$.latestVersion").value("3.0.0"))
                .andExpect(jsonPath("$.reason").value("current version is not supported."));
    }

    @Test
    @DisplayName("POST /api/v1/app/version-check - 비호환이지만 강제 업데이트 불필요")
    void checkVersion_incompatible_noForceUpdate() throws Exception {
        // given
        CheckAppVersionApiRequest request = new CheckAppVersionApiRequest();
        request.setPlatform(com.platform.sosangongin.domains.common.ClientPlatform.IOS);
        request.setAppVersion("1.5.0");

        CheckAppVersionResult result = CheckAppVersionResult.builder()
                .compatible(false)
                .forceUpdateRequired(false)
                .latestVersion("2.0.0")
                .reason("current version is not supported.")
                .build();

        given(checkAppVersionUsecase.check(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/app/version-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatible").value(false))
                .andExpect(jsonPath("$.forceUpdateRequired").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/app/version-check - 버전 정보 없는 플랫폼")
    void checkVersion_noVersionInfo() throws Exception {
        // given
        CheckAppVersionApiRequest request = new CheckAppVersionApiRequest();
        request.setPlatform(com.platform.sosangongin.domains.common.ClientPlatform.IOS);
        request.setAppVersion("1.0.0");

        CheckAppVersionResult result = CheckAppVersionResult.builder()
                .compatible(true)
                .forceUpdateRequired(false)
                .build();

        given(checkAppVersionUsecase.check(any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/app/version-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatible").value(true))
                .andExpect(jsonPath("$.forceUpdateRequired").value(false))
                .andExpect(jsonPath("$.latestVersion").isEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/app/status - 점검 중이 아닌 경우")
    void checkStatus_notUnderMaintenance() throws Exception {
        // given
        CheckAppStatusResult result = CheckAppStatusResult.builder()
                .underMaintenance(false)
                .build();

        given(checkAppStatusUsecase.check()).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/app/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.underMaintenance").value(false))
                .andExpect(jsonPath("$.reason").isEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/app/status - 점검 중인 경우")
    void checkStatus_underMaintenance() throws Exception {
        // given
        LocalDateTime startedAt = LocalDateTime.of(2026, 3, 27, 0, 0);
        LocalDateTime endedAt = LocalDateTime.of(2026, 3, 27, 6, 0);

        CheckAppStatusResult result = CheckAppStatusResult.builder()
                .underMaintenance(true)
                .reason("서버 정기 점검")
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();

        given(checkAppStatusUsecase.check()).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/app/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.underMaintenance").value(true))
                .andExpect(jsonPath("$.reason").value("서버 정기 점검"))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.endedAt").exists());
    }
}
