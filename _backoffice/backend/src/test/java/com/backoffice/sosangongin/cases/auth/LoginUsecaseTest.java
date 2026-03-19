package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.AccountBackoffice;
import com.backoffice.sosangongin.domains.account.AccountBackofficeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class LoginUsecaseTest {

    @Autowired
    private LoginUsecase loginUsecase;

    @Autowired
    private AccountBackofficeRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountBackoffice testAccount;
    private MockHttpSession session;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        testAccount = AccountBackoffice.builder()
                .userId(UUID.randomUUID())
                .loginId("testuser")
                .password(passwordEncoder.encode("password123"))
                .failedLoginAttempts(0)
                .isLocked(false)
                .build();
        accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("로그인 성공: 세션에 ACCOUNT_ID가 저장되고, 실패 횟수가 초기화된다")
    void login_success_setsSessionAndResetsAttempts() {
        // given
        testAccount.incrementFailedLoginAttempts(); // 실패 횟수를 1로 만듦
        accountRepository.save(testAccount);

        // when
        loginUsecase.login("testuser", "password123", session);

        // then
        UUID accountId = (UUID) session.getAttribute("ACCOUNT_ID");
        assertNotNull(accountId);
        assertEquals(testAccount.getId(), accountId);

        AccountBackoffice freshAccount = accountRepository.findById(testAccount.getId()).get();
        assertEquals(0, freshAccount.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호 입력 시 예외가 발생하고, 실패 횟수가 증가한다")
    void login_fail_wrongPassword_throwsExceptionAndIncrementsAttempts() {
        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> loginUsecase.login("testuser", "wrongpassword", session));

        AccountBackoffice freshAccount = accountRepository.findById(testAccount.getId()).get();
        assertEquals(1, freshAccount.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("로그인 5회 실패: 계정이 잠금 처리된다")
    void login_fail_fiveTimes_locksAccount() {
        // given
        for (int i = 0; i < 4; i++) {
            assertThrows(IllegalArgumentException.class,
                    () -> loginUsecase.login("testuser", "wrongpassword", session));
        }

        // when: 5번째 실패
        assertThrows(IllegalArgumentException.class,
                () -> loginUsecase.login("testuser", "wrongpassword", session));

        // then
        AccountBackoffice lockedAccount = accountRepository.findById(testAccount.getId()).get();
        assertTrue(lockedAccount.isLocked());
        assertEquals(5, lockedAccount.getFailedLoginAttempts());
        assertNotNull(lockedAccount.getLockedAt());
    }

    @Test
    @DisplayName("잠긴 계정 로그인 시도: IllegalStateException 예외가 발생한다")
    void login_fail_lockedAccount_throwsIllegalStateException() {
        // given
        testAccount.lockAccount();
        accountRepository.save(testAccount);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> loginUsecase.login("testuser", "password123", session));
    }
}
