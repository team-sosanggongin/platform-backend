package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ChangePasswordUsecaseTest {

    @Autowired
    private ChangePasswordUsecase changePasswordUsecase;

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
                .password(passwordEncoder.encode("oldPassword123"))
                .isPasswordExpired(true)
                .build();
        adminRepository.save(testAccount);
    }

    @Test
    @DisplayName("비밀번호 변경 성공: 비밀번호가 변경되고 만료 플래그가 해제된다")
    void changePassword_success() {
        // when
        ChangePasswordResult result = changePasswordUsecase.changePassword(
                testAccount.getId(), "oldPassword123", "newPassword456");

        // then
        assertTrue(result.isSuccess());

        BackofficeAdmin updated = adminRepository.findById(testAccount.getId()).get();
        assertTrue(passwordEncoder.matches("newPassword456", updated.getPassword()));
        assertFalse(updated.isPasswordExpired());
        assertNotNull(updated.getPasswordChangedAt());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 현재 비밀번호 불일치")
    void changePassword_fail_wrongCurrentPassword() {
        // when
        ChangePasswordResult result = changePasswordUsecase.changePassword(
                testAccount.getId(), "wrongPassword", "newPassword456");

        // then
        assertFalse(result.isSuccess());
        assertEquals(ChangePasswordResult.Status.CURRENT_PASSWORD_MISMATCH, result.getStatus());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 새 비밀번호가 현재와 동일")
    void changePassword_fail_sameAsCurrentPassword() {
        // when
        ChangePasswordResult result = changePasswordUsecase.changePassword(
                testAccount.getId(), "oldPassword123", "oldPassword123");

        // then
        assertFalse(result.isSuccess());
        assertEquals(ChangePasswordResult.Status.SAME_AS_CURRENT, result.getStatus());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 존재하지 않는 계정")
    void changePassword_fail_accountNotFound() {
        // when
        ChangePasswordResult result = changePasswordUsecase.changePassword(
                UUID.randomUUID(), "oldPassword123", "newPassword456");

        // then
        assertFalse(result.isSuccess());
        assertEquals(ChangePasswordResult.Status.ACCOUNT_NOT_FOUND, result.getStatus());
    }
}