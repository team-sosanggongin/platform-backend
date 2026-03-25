package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangePasswordUsecase {
    private final BackofficeAdminRepository backofficeAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(UUID accountId, String currentPassword, String newPassword) {
        BackofficeAdmin account = backofficeAdminRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.");
        }

        account.changePassword(passwordEncoder.encode(newPassword));
    }
}
