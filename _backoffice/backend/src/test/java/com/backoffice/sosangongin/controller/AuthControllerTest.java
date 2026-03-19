package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.auth.LoginRequest;
import com.backoffice.sosangongin.domains.account.AccountBackoffice;
import com.backoffice.sosangongin.domains.account.AccountBackofficeRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private AccountBackofficeRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountBackoffice testAccount;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        testAccount = AccountBackoffice.builder()
                .userId(UUID.randomUUID())
                .loginId("testuser")
                .password(passwordEncoder.encode("password123"))
                .build();
        accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("로그인 성공: 200 OK 반환")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
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
        accountRepository.save(testAccount);
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
        AccountBackoffice accountAfterFailure = accountRepository.findById(testAccount.getId()).get();
        assertEquals(1, accountAfterFailure.getFailedLoginAttempts());

        // then: 로그인 성공
        LoginRequest successRequest = new LoginRequest("testuser", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successRequest)))
                .andExpect(status().isOk());

        // then: 실패 횟수 초기화 검증
        AccountBackoffice accountAfterSuccess = accountRepository.findById(testAccount.getId()).get();
        assertEquals(0, accountAfterSuccess.getFailedLoginAttempts());
    }
}
