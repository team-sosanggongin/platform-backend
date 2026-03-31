package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.auth.LoginRequest;
import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BackofficeAdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BackofficeAdmin testAccount;

    @BeforeEach
    void setup() {
        adminRepository.deleteAll();
        testAccount = BackofficeAdmin.builder()
                .loginId("testuser")
                .name("테스트관리자")
                .password(passwordEncoder.encode("password123"))
                .isPasswordExpired(false)
                .build();
        adminRepository.save(testAccount);
    }

    @Test
    @DisplayName("로그인 성공: 200 OK + LoginResponse 반환")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(testAccount.getId().toString()))
                .andExpect(jsonPath("$.name").value("테스트관리자"))
                .andExpect(jsonPath("$.passwordExpired").value(false));
    }

    @Test
    @DisplayName("로그인 실패 (자격 증명 오류): 401 Unauthorized 반환")
    void login_fail_wrongCredentials() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 (잠긴 계정): 403 Forbidden 반환")
    void login_fail_lockedAccount() throws Exception {
        // given
        testAccount.lockAccount();
        adminRepository.save(testAccount);
        LoginRequest request = new LoginRequest("testuser", "password123");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 실패 후 성공: 실패 횟수가 초기화된다")
    void login_success_afterFailure_resetsAttempts() throws Exception {
        // given: 로그인 1회 실패
        LoginRequest failRequest = new LoginRequest("testuser", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failRequest)))
                .andExpect(status().isUnauthorized());

        // when: 실패 횟수 검증
        BackofficeAdmin accountAfterFailure = adminRepository.findById(testAccount.getId()).get();
        assertEquals(1, accountAfterFailure.getFailedLoginAttempts());

        // then: 로그인 성공
        LoginRequest successRequest = new LoginRequest("testuser", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successRequest)))
                .andExpect(status().isOk());

        // then: 실패 횟수 초기화 검증
        BackofficeAdmin accountAfterSuccess = adminRepository.findById(testAccount.getId()).get();
        assertEquals(0, accountAfterSuccess.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("GET /me: 로그인 후 세션으로 내 정보 조회 성공")
    void me_success_afterLogin() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        var session = loginResult.getRequest().getSession();

        mockMvc.perform(get("/api/auth/me").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(testAccount.getId().toString()))
                .andExpect(jsonPath("$.name").value("테스트관리자"));
    }

    @Test
    @DisplayName("GET /me: 세션 없으면 401")
    void me_fail_noSession() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}