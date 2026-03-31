package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GetMyInfoUsecaseTest {

    @Autowired
    private GetMyInfoUsecase getMyInfoUsecase;

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
                .isPasswordExpired(false)
                .build();
        adminRepository.save(testAccount);
    }

    @Test
    @DisplayName("내 정보 조회 성공: BackofficeAdmin이 반환된다")
    void getMyInfo_success() {
        BackofficeAdmin result = getMyInfoUsecase.getMyInfo(testAccount.getId());

        assertThat(result.getId()).isEqualTo(testAccount.getId());
        assertThat(result.getName()).isEqualTo("테스트관리자");
        assertThat(result.getLoginId()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("내 정보 조회 실패: 존재하지 않는 계정이면 EntityNotFoundException")
    void getMyInfo_fail_notFound() {
        assertThatThrownBy(() -> getMyInfoUsecase.getMyInfo(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}