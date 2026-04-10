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

import java.util.HashMap;
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
    @DisplayName("공지사항 생성 → 201, 새 필드 포함")
    void create_notice() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "테스트 공지");
        payload.put("content", "테스트 내용");
        payload.put("startsAt", "2026-04-04T00:00:00");
        payload.put("isServiceMaintenance", true);
        payload.put("maintenanceStartAt", "2026-04-04T02:00:00");
        payload.put("maintenanceEndAt", "2026-04-04T04:00:00");

        mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 공지"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.isServiceMaintenance").value(true))
                .andExpect(jsonPath("$.maintenanceStartAt").exists())
                .andExpect(jsonPath("$.maintenanceEndAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("공지사항 페이징 조회 → 200, PageResponse 형태")
    void findAll_paged() throws Exception {
        createNotice("페이징 테스트 1");
        createNotice("페이징 테스트 2");

        mockMvc.perform(get("/api/notice")
                        .session(rootSession)
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(8))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    @DisplayName("공지사항 검색 → keyword로 필터링")
    void search_by_keyword() throws Exception {
        createNotice("검색용 특별 공지");
        createNotice("일반 공지");

        mockMvc.perform(get("/api/notice")
                        .session(rootSession)
                        .param("keyword", "특별")
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("검색용 특별 공지"));
    }

    @Test
    @DisplayName("공지사항 상태 변경 → PUBLISHED 200")
    void changeStatus_to_published() throws Exception {
        Long noticeId = createNotice("상태변경 테스트");

        String statusBody = objectMapper.writeValueAsString(
                Map.of("status", "PUBLISHED"));

        mockMvc.perform(patch("/api/notice/" + noticeId + "/status")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("SCHEDULED 상태 변경 → scheduledAt 없으면 실패")
    void changeStatus_to_scheduled_without_scheduledAt() throws Exception {
        Long noticeId = createNotice("예약 테스트");

        String statusBody = objectMapper.writeValueAsString(
                Map.of("status", "SCHEDULED"));

        mockMvc.perform(patch("/api/notice/" + noticeId + "/status")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SCHEDULED 상태 변경 → scheduledAt 있으면 성공")
    void changeStatus_to_scheduled_with_scheduledAt() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "예약 공지");
        payload.put("content", "예약 내용");
        payload.put("startsAt", "2026-04-10T00:00:00");
        payload.put("scheduledAt", "2026-04-09T10:00:00");
        payload.put("isServiceMaintenance", false);

        MvcResult createResult = mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        Long noticeId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        String statusBody = objectMapper.writeValueAsString(
                Map.of("status", "SCHEDULED"));

        mockMvc.perform(patch("/api/notice/" + noticeId + "/status")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 삭제 → 204")
    void delete_notice() throws Exception {
        Long noticeId = createNotice("삭제 테스트");

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

    private Long createNotice(String title) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("content", "내용");
        payload.put("startsAt", "2026-04-04T00:00:00");
        payload.put("isServiceMaintenance", false);

        MvcResult result = mockMvc.perform(post("/api/notice")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }
}
