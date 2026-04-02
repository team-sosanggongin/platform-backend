package com.backoffice.sosangongin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 → 200")
    void login_success() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "root"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("root"))
                .andExpect(jsonPath("$.root").value(true));
    }

    @Test
    @DisplayName("로그인 실패 (잘못된 비밀번호) → 401")
    void login_fail_wrongPassword() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "wrong"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("미인증 접근 → 401")
    void unauthenticated_access_returns_401() throws Exception {
        mockMvc.perform(get("/api/some-protected-resource"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 인증 필요 경로 접근 → 401 아님")
    void authenticated_access_after_login() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "root"));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(get("/api/some-protected-resource")
                        .session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그아웃 → 200")
    void logout_success() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "root"));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(post("/api/auth/logout")
                        .session(session))
                .andExpect(status().isOk());
    }
}
