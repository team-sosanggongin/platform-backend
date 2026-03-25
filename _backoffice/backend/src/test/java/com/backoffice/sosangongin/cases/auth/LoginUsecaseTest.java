package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class LoginUsecaseTest {

    @Autowired
    private LoginUsecase loginUsecase;

    @Autowired
    private BackofficeAdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BackofficeAdmin testAccount;
    private MockHttpSession session;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        testAccount = BackofficeAdmin.builder()
                .loginId("testuser")
                .name("테스트관리자")
                .password(passwordEncoder.encode("password123"))
                .failedLoginAttempts(0)
                .isLocked(false)
                .isPasswordExpired(false)
                .build();
        adminRepository.save(testAccount);
    }

    @Test
    @DisplayName("로그인 성공: 세션에 ACCOUNT_ID가 저장되고, 실패 횟수가 초기화된다")
    void login_success_setsSessionAndResetsAttempts() {
        // given
        testAccount.incrementFailedLoginAttempts();
        adminRepository.save(testAccount);

        // when
        LoginResult result = loginUsecase.login("testuser", "password123", request, session);

        // then
        assertNotNull(session.getAttribute("ACCOUNT_ID"));
        assertEquals(testAccount.getId(), session.getAttribute("ACCOUNT_ID"));
        assertFalse(result.isPasswordExpired());

        BackofficeAdmin freshAccount = adminRepository.findById(testAccount.getId()).get();
        assertEquals(0, freshAccount.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("로그인 성공 (비밀번호 만료): passwordExpired가 true로 반환된다")
    void login_success_passwordExpired_returnsTrue() {
        // given
        BackofficeAdmin expiredAccount = BackofficeAdmin.builder()
                .loginId("expireduser")
                .name("만료관리자")
                .password(passwordEncoder.encode("password123"))
                .isPasswordExpired(true)
                .build();
        adminRepository.save(expiredAccount);

        // when
        LoginResult result = loginUsecase.login("expireduser", "password123", request, session);

        // then
        assertNotNull(session.getAttribute("ACCOUNT_ID"));
        assertTrue(result.isPasswordExpired());
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호 입력 시 예외가 발생하고, 실패 횟수가 증가한다")
    void login_fail_wrongPassword_throwsExceptionAndIncrementsAttempts() {
        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> loginUsecase.login("testuser", "wrongpassword", request, session));

        BackofficeAdmin freshAccount = adminRepository.findById(testAccount.getId()).get();
        assertEquals(1, freshAccount.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("로그인 5회 실패: 계정이 잠금 처리된다")
    void login_fail_fiveTimes_locksAccount() {
        // given
        for (int i = 0; i < 4; i++) {
            assertThrows(IllegalArgumentException.class,
                    () -> loginUsecase.login("testuser", "wrongpassword", request, session));
        }

        // when: 5번째 실패
        assertThrows(IllegalArgumentException.class,
                () -> loginUsecase.login("testuser", "wrongpassword", request, session));

        // then
        BackofficeAdmin lockedAccount = adminRepository.findById(testAccount.getId()).get();
        assertTrue(lockedAccount.isLocked());
        assertEquals(5, lockedAccount.getFailedLoginAttempts());
        assertNotNull(lockedAccount.getLockedAt());
    }

    @Test
    @DisplayName("잠긴 계정 로그인 시도: IllegalStateException 예외가 발생한다")
    void login_fail_lockedAccount_throwsIllegalStateException() {
        // given
        testAccount.lockAccount();
        adminRepository.save(testAccount);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> loginUsecase.login("testuser", "password123", request, session));
    }
}