package com.backoffice.sosangongin.domains.loginHistory;

import com.backoffice.sosangongin.cases.auth.LoginUsecase;
import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LoginHistoryIntegrationTest {

    @Autowired
    private LoginUsecase loginUsecase;

    @Autowired
    private BackofficeAdminRepository adminRepository;

    @Autowired
    private AdminLoginHistoryRepository adminLoginHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BackofficeAdmin testAccount;
    private MockHttpSession session;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("User-Agent", "Test-Agent");

        adminLoginHistoryRepository.deleteAll();
        adminRepository.deleteAll();

        testAccount = BackofficeAdmin.builder()
                .loginId("history_test_user")
                .name("이력테스트관리자")
                .password(passwordEncoder.encode("password123"))
                .isPasswordExpired(false)
                .build();
        adminRepository.save(testAccount);
    }

    @Test
    @DisplayName("로그인 성공 시: 성공 이력이 기록된다")
    void recordHistory_onLoginSuccess() {
        // when
        loginUsecase.login("history_test_user", "password123", request, session);

        // then
        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertEquals(1, histories.size());
        AdminLoginHistory history = histories.get(0);

        assertEquals(testAccount.getId(), history.getAccountId());
        assertTrue(history.isSuccess());
        assertEquals("127.0.0.1", history.getIpAddress());
        assertEquals("Test-Agent", history.getUserAgent());
    }

    @Test
    @DisplayName("로그인 실패 시: 실패 이력이 기록된다")
    void recordHistory_onLoginFailure() {
        // when
        assertThrows(IllegalArgumentException.class, () -> {
            loginUsecase.login("history_test_user", "wrong_password", request, session);
        });

        // then
        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertEquals(1, histories.size());
        AdminLoginHistory history = histories.get(0);

        assertEquals(testAccount.getId(), history.getAccountId());
        assertFalse(history.isSuccess());
    }

    @Test
    @DisplayName("잠긴 계정으로 로그인 시도 시: 실패 이력이 기록된다")
    void recordHistory_onLockedAccountLoginAttempt() {
        // given
        testAccount.lockAccount();
        adminRepository.save(testAccount);

        // when
        assertThrows(IllegalStateException.class, () -> {
            loginUsecase.login("history_test_user", "password123", request, session);
        });

        // then
        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertEquals(1, histories.size());
        AdminLoginHistory history = histories.get(0);

        assertEquals(testAccount.getId(), history.getAccountId());
        assertFalse(history.isSuccess());
    }
}