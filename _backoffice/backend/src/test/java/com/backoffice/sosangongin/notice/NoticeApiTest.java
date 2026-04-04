package com.backoffice.sosangongin.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoticeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession rootSession;

    @BeforeEach
    void login() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "root"));

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        rootSession = (MockHttpSession) result.getRequest().getSession();
    }

    @Test
    @DisplayName("공지사항 생성 → 201")
    void create_notice() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("title", "테스트 공지", "content", "테스트 내용",
                        "startsAt", "2026-04-04T00:00:00",
                        "isServiceMaintenance", false));

        mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 공지"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @DisplayName("공지사항 전체 조회 → 200")
    void findAll_notices() throws Exception {
        mockMvc.perform(get("/api/notice")
                        .session(rootSession))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 상태 변경 → 200")
    void changeStatus() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("title", "상태변경 테스트", "content", "내용",
                        "startsAt", "2026-04-04T00:00:00",
                        "isServiceMaintenance", false));

        MvcResult createResult = mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        Long noticeId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        String statusBody = objectMapper.writeValueAsString(
                Map.of("status", "PUBLISHED"));

        mockMvc.perform(patch("/api/notice/" + noticeId + "/status")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 삭제 → 204")
    void delete_notice() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("title", "삭제 테스트", "content", "내용",
                        "startsAt", "2026-04-04T00:00:00",
                        "isServiceMaintenance", false));

        MvcResult createResult = mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        Long noticeId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(delete("/api/notice/" + noticeId)
                        .session(rootSession))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("미인증 접근 → 401")
    void unauthenticated_access() throws Exception {
        mockMvc.perform(get("/api/notice"))
                .andExpect(status().isUnauthorized());
    }
}
