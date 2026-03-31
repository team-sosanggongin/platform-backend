package com.backoffice.sosangongin.cases.auth;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @BeforeEach
    void setup() {
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
    @DisplayName("로그인 성공: BackofficeAdmin이 반환되고, 실패 횟수가 초기화된다")
    void login_success_returnsAdminAndResetsAttempts() {
        testAccount.incrementFailedLoginAttempts();
        adminRepository.save(testAccount);

        BackofficeAdmin result = loginUsecase.login("testuser", "password123", "127.0.0.1", "Test-Agent");

        assertThat(result.getId()).isEqualTo(testAccount.getId());
        assertThat(result.getName()).isEqualTo("테스트관리자");
        assertThat(result.isPasswordExpired()).isFalse();

        BackofficeAdmin freshAccount = adminRepository.findById(testAccount.getId()).get();
        assertThat(freshAccount.getFailedLoginAttempts()).isZero();
    }

    @Test
    @DisplayName("로그인 성공 (비밀번호 만료): passwordExpired가 true로 반환된다")
    void login_success_passwordExpired_returnsTrue() {
        BackofficeAdmin expiredAccount = BackofficeAdmin.builder()
                .loginId("expireduser")
                .name("만료관리자")
                .password(passwordEncoder.encode("password123"))
                .isPasswordExpired(true)
                .build();
        adminRepository.save(expiredAccount);

        BackofficeAdmin result = loginUsecase.login("expireduser", "password123", "127.0.0.1", "Test-Agent");

        assertThat(result.isPasswordExpired()).isTrue();
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호 입력 시 InvalidCredentialsException, 실패 횟수 증가")
    void login_fail_wrongPassword_throwsInvalidCredentials() {
        assertThatThrownBy(() -> loginUsecase.login("testuser", "wrongpassword", "127.0.0.1", "Test-Agent"))
                .isInstanceOf(InvalidCredentialsException.class);

        BackofficeAdmin freshAccount = adminRepository.findById(testAccount.getId()).get();
        assertThat(freshAccount.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 아이디로 InvalidCredentialsException")
    void login_fail_unknownUser_throwsInvalidCredentials() {
        assertThatThrownBy(() -> loginUsecase.login("unknown", "password123", "127.0.0.1", "Test-Agent"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("로그인 5회 실패: 계정이 잠금 처리된다")
    void login_fail_fiveTimes_locksAccount() {
        for (int i = 0; i < 5; i++) {
            try {
                loginUsecase.login("testuser", "wrongpassword", "127.0.0.1", "Test-Agent");
            } catch (InvalidCredentialsException ignored) {
            }
        }

        BackofficeAdmin lockedAccount = adminRepository.findById(testAccount.getId()).get();
        assertThat(lockedAccount.isLocked()).isTrue();
        assertThat(lockedAccount.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(lockedAccount.getLockedAt()).isNotNull();
    }

    @Test
    @DisplayName("잠긴 계정 로그인 시도: AccountLockedException")
    void login_fail_lockedAccount_throwsAccountLocked() {
        testAccount.lockAccount();
        adminRepository.save(testAccount);

        assertThatThrownBy(() -> loginUsecase.login("testuser", "password123", "127.0.0.1", "Test-Agent"))
                .isInstanceOf(AccountLockedException.class);
    }
}
