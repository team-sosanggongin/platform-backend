package com.backoffice.sosangongin.domains.loginHistory;

import com.backoffice.sosangongin.cases.auth.LoginUsecase;
import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.errors.AccountLockedException;
import com.backoffice.sosangongin.errors.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @BeforeEach
    void setup() {
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
        loginUsecase.login("history_test_user", "password123", "127.0.0.1", "Test-Agent");

        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        AdminLoginHistory history = histories.get(0);

        assertThat(history.getAccountId()).isEqualTo(testAccount.getId());
        assertThat(history.isSuccess()).isTrue();
        assertThat(history.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(history.getUserAgent()).isEqualTo("Test-Agent");
    }

    @Test
    @DisplayName("로그인 실패 시: 실패 이력이 기록된다")
    void recordHistory_onLoginFailure() {
        assertThatThrownBy(() ->
                loginUsecase.login("history_test_user", "wrong_password", "127.0.0.1", "Test-Agent"))
                .isInstanceOf(InvalidCredentialsException.class);

        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        AdminLoginHistory history = histories.get(0);

        assertThat(history.getAccountId()).isEqualTo(testAccount.getId());
        assertThat(history.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("잠긴 계정으로 로그인 시도 시: 실패 이력이 기록된다")
    void recordHistory_onLockedAccountLoginAttempt() {
        testAccount.lockAccount();
        adminRepository.save(testAccount);

        assertThatThrownBy(() ->
                loginUsecase.login("history_test_user", "password123", "127.0.0.1", "Test-Agent"))
                .isInstanceOf(AccountLockedException.class);

        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        AdminLoginHistory history = histories.get(0);

        assertThat(history.getAccountId()).isEqualTo(testAccount.getId());
        assertThat(history.isSuccess()).isFalse();
    }
}
