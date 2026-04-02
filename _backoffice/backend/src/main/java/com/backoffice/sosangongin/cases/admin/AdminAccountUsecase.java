package com.backoffice.sosangongin.cases.admin;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.errors.DuplicateResourceException;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminAccountUsecase {

    private final BackofficeAdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public BackofficeAdmin createAdmin(CreateAdminCommand command, UUID createdBy) {
        if (adminRepository.existsByLoginId(command.getLoginId())) {
            throw new DuplicateResourceException("이미 존재하는 아이디입니다. loginId=" + command.getLoginId());
        }

        // TODO: SMS 구현 시 아래 로그 제거 후 SMS 발송으로 교체 예정
        //  - 발송 내용: loginId, 임시비밀번호, 로그인 URL
        //  - 전송할 URL 형식 확인 필요
        String tempPassword = command.getLoginId();
        log.info("[임시 비밀번호] loginId={}, tempPassword={}", command.getLoginId(), tempPassword);

        BackofficeAdmin admin = BackofficeAdmin.builder()
                .loginId(command.getLoginId())
                .password(passwordEncoder.encode(tempPassword))
                .name(command.getName())
                .phone(command.getPhone())
                .createdBy(createdBy)
                .isPasswordExpired(true)
                .build();

        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public List<BackofficeAdmin> getAllAdmins() {
        return adminRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public BackofficeAdmin getAdmin(UUID adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다. id=" + adminId));
    }

    public BackofficeAdmin updateAdmin(UUID adminId, UpdateAdminCommand command) {
        BackofficeAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다. id=" + adminId));

        admin.updateInfo(command.getName(), command.getEmail(), command.getPhone());
        return admin;
    }

    public void deleteAdmin(UUID adminId) {
        BackofficeAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다. id=" + adminId));

        admin.delete();
    }

    public void unlockAdmin(UUID adminId) {
        BackofficeAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다. id=" + adminId));

        admin.unlockAccount();
    }

    public void restoreAdmin(UUID adminId) {
        BackofficeAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다. id=" + adminId));

        admin.restore();
        admin.unlockAccount();
    }
}