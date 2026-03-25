package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.cases.notices.*;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.domains.notices.NoticeStatus;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NoticeController.class)
@Import({SecurityConfig.class, WebMvcConfig.class, CommonResultResponseAdvice.class})
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicNoticeUseCase publicNoticeUseCase;

    @MockBean
    private JwtService jwtService;

    private UsernamePasswordAuthenticationToken createAuth() {
        return new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null, null);
    }

    @Test
    @DisplayName("GET /api/v1/notices - 인증된 유저 공지사항 목록 조회 성공")
    void getNotices_authenticated_success() throws Exception {
        // given
        PublicNoticeVo vo = new PublicNoticeVo(
                1L, "서비스 점검 안내", "3월 25일 점검 예정입니다.", "관리자",
                NoticeStatus.PUBLISHED, true, 100L,
                LocalDateTime.of(2026, 3, 20, 0, 0),
                LocalDateTime.of(2026, 4, 20, 0, 0),
                LocalDateTime.of(2026, 3, 19, 10, 0),
                LocalDateTime.of(2026, 3, 19, 10, 0)
        );

        PublicNoticeResult result = new PublicNoticeResult(
                HttpStatus.OK, "",
                new PageImpl<>(List.of(vo), PageRequest.of(0, 10), 1));

        given(publicNoticeUseCase.getNoticeList(any(PublicNoticeRequest.class))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/notices")
                        .with(authentication(createAuth()))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicNotices.content[0].title").value("서비스 점검 안내"))
                .andExpect(jsonPath("$.publicNotices.content[0].authorName").value("관리자"))
                .andExpect(jsonPath("$.publicNotices.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/notices - 인증 없이 접근 시 거부")
    void getNotices_unauthenticated_rejected() throws Exception {
        mockMvc.perform(get("/api/v1/notices"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /api/v1/notices/{noticeId} - 공지사항 상세 조회 성공")
    void getNoticeDetail_success() throws Exception {
        // given
        PublicNoticeVo vo = new PublicNoticeVo(
                1L, "업데이트 안내", "v2.0 업데이트 내용입니다.", "관리자",
                NoticeStatus.PUBLISHED, false, 50L,
                LocalDateTime.of(2026, 3, 20, 0, 0),
                LocalDateTime.of(2026, 4, 20, 0, 0),
                LocalDateTime.of(2026, 3, 19, 10, 0),
                LocalDateTime.of(2026, 3, 19, 10, 0)
        );

        PublicNoticeDetailResult result = PublicNoticeDetailResult.builder()
                .httpStatus(HttpStatus.OK)
                .vo(vo)
                .build();

        given(publicNoticeUseCase.getNoticeDetail(eq(1L))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/notices/{noticeId}", 1L)
                        .with(authentication(createAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicNotice.title").value("업데이트 안내"))
                .andExpect(jsonPath("$.publicNotice.viewCount").value(50));
    }

    @Test
    @DisplayName("GET /api/v1/notices/{noticeId} - 존재하지 않는 공지사항 404")
    void getNoticeDetail_notFound() throws Exception {
        // given
        PublicNoticeDetailResult result = PublicNoticeDetailResult.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("notice is not found")
                .build();

        given(publicNoticeUseCase.getNoticeDetail(eq(999L))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/notices/{noticeId}", 999L)
                        .with(authentication(createAuth())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("notice is not found"));
    }

    @Test
    @DisplayName("GET /api/v1/notices/{noticeId} - 인증 없이 접근 시 거부")
    void getNoticeDetail_unauthenticated_rejected() throws Exception {
        mockMvc.perform(get("/api/v1/notices/{noticeId}", 1L))
                .andExpect(status().is4xxClientError());
    }
}
