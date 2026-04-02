package com.backoffice.sosangongin.cases.admin;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.errors.DuplicateResourceException;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AdminAccountUsecaseTest {

    @Autowired
    private AdminAccountUsecase adminAccountUsecase;

    @Autowired
    private BackofficeAdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID rootId;

    @BeforeEach
    void setup() {
        adminRepository.deleteAll();

        BackofficeAdmin root = BackofficeAdmin.builder()
                .loginId("root")
                .password(passwordEncoder.encode("root123"))
                .name("루트관리자")
                .isPasswordExpired(false)
                .build();
        adminRepository.save(root);
        rootId = root.getId();
    }

    @Test
    @DisplayName("관리자 생성: 임시 비밀번호는 loginId와 동일, isPasswordExpired=true")
    void createAdmin_success() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("newadmin")
                .name("새관리자")
                .phone("010-1234-5678")
                .build();

        BackofficeAdmin result = adminAccountUsecase.createAdmin(command, rootId);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getLoginId()).isEqualTo("newadmin");
        assertThat(result.getName()).isEqualTo("새관리자");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.isPasswordExpired()).isTrue();
        assertThat(result.getCreatedBy()).isEqualTo(rootId);
        assertThat(passwordEncoder.matches("newadmin", result.getPassword())).isTrue();
    }

    @Test
    @DisplayName("관리자 생성: 중복 loginId면 DuplicateResourceException")
    void createAdmin_duplicateLoginId() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("root")
                .name("중복관리자")
                .build();

        assertThatThrownBy(() -> adminAccountUsecase.createAdmin(command, rootId))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("관리자 목록 조회: soft delete 포함 전체 반환")
    void getAllAdmins_includesSoftDeleted() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("admin2")
                .name("관리자2")
                .build();
        BackofficeAdmin created = adminAccountUsecase.createAdmin(command, rootId);
        adminAccountUsecase.deleteAdmin(created.getId());

        List<BackofficeAdmin> result = adminAccountUsecase.getAllAdmins();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("관리자 상세 조회: 존재하는 관리자 반환")
    void getAdmin_success() {
        BackofficeAdmin result = adminAccountUsecase.getAdmin(rootId);

        assertThat(result.getLoginId()).isEqualTo("root");
    }

    @Test
    @DisplayName("관리자 상세 조회: 존재하지 않으면 EntityNotFoundException")
    void getAdmin_notFound() {
        assertThatThrownBy(() -> adminAccountUsecase.getAdmin(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("관리자 수정: 이름, 이메일, 전화번호 변경")
    void updateAdmin_success() {
        UpdateAdminCommand command = UpdateAdminCommand.builder()
                .name("수정된이름")
                .email("new@email.com")
                .phone("010-9999-9999")
                .build();

        BackofficeAdmin result = adminAccountUsecase.updateAdmin(rootId, command);

        assertThat(result.getName()).isEqualTo("수정된이름");
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        assertThat(result.getPhone()).isEqualTo("010-9999-9999");
    }

    @Test
    @DisplayName("관리자 삭제: soft delete 처리")
    void deleteAdmin_softDeletes() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("todelete")
                .name("삭제대상")
                .build();
        BackofficeAdmin created = adminAccountUsecase.createAdmin(command, rootId);

        adminAccountUsecase.deleteAdmin(created.getId());

        BackofficeAdmin deleted = adminRepository.findById(created.getId()).get();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("계정 잠금 해제: isLocked=false, failedLoginAttempts=0")
    void unlockAdmin_success() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("lockeduser")
                .name("잠긴관리자")
                .build();
        BackofficeAdmin created = adminAccountUsecase.createAdmin(command, rootId);
        created.lockAccount();
        adminRepository.save(created);

        adminAccountUsecase.unlockAdmin(created.getId());

        BackofficeAdmin unlocked = adminRepository.findById(created.getId()).get();
        assertThat(unlocked.isLocked()).isFalse();
        assertThat(unlocked.getFailedLoginAttempts()).isZero();
    }

    @Test
    @DisplayName("계정 복구: deletedAt이 null로 복원")
    void restoreAdmin_success() {
        CreateAdminCommand command = CreateAdminCommand.builder()
                .loginId("restoreuser")
                .name("복구대상")
                .build();
        BackofficeAdmin created = adminAccountUsecase.createAdmin(command, rootId);
        adminAccountUsecase.deleteAdmin(created.getId());

        adminAccountUsecase.restoreAdmin(created.getId());

        BackofficeAdmin restored = adminRepository.findById(created.getId()).get();
        assertThat(restored.getDeletedAt()).isNull();
    }
}