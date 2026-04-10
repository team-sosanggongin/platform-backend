package com.backoffice.sosangongin.account.service;

import com.backoffice.sosangongin.account.dto.*;
import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.auth.repos.AdminRepository;
import com.backoffice.sosangongin.global.exception.DuplicateResourceException;
import com.backoffice.sosangongin.global.exception.EntityNotFoundException;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import com.backoffice.sosangongin.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public BackofficeAdmin create(AccountCreateRequest request) {
        if (adminRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicateResourceException("이미 존재하는 아이디입니다.");
        }

        return adminRepository.save(
                BackofficeAdmin.builder()
                        .loginId(request.getLoginId())
                        .password(passwordEncoder.encode(request.getLoginId()))
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .isRoot(false)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<BackofficeAdmin> findAll() {
        return adminRepository.findByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public BackofficeAdmin findById(UUID id) {
        return adminRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계정입니다."));
    }

    @Transactional
    public BackofficeAdmin update(UUID id, AccountUpdateRequest request) {
        BackofficeAdmin admin = findById(id);
        if (admin.isRoot()) {
            throw new PermissionDeniedException("root 계정은 수정할 수 없습니다.");
        }
        admin.update(request.getName(), request.getEmail(), request.getPhoneNumber());
        return admin;
    }

    @Transactional
    public void delete(UUID id) {
        BackofficeAdmin admin = findById(id);
        if (admin.isRoot()) {
            throw new PermissionDeniedException("root 계정은 삭제할 수 없습니다.");
        }
        admin.delete();
    }

    @Transactional
    public void unlock(UUID id) {
        BackofficeAdmin admin = findById(id);
        if (admin.isRoot()) {
            throw new PermissionDeniedException("root 계정은 잠금 해제 대상이 아닙니다.");
        }
        admin.unlock();
    }

    @Transactional
    public void changePassword(UUID id, PasswordChangeRequest request) {
        BackofficeAdmin admin = findById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }
        admin.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}