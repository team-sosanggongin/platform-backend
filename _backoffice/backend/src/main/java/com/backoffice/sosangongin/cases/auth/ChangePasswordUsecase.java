package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangePasswordUsecase {
    private final BackofficeAdminRepository backofficeAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ChangePasswordResult changePassword(UUID accountId, String currentPassword, String newPassword) {
        Optional<BackofficeAdmin> accountOpt = backofficeAdminRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            return ChangePasswordResult.builder()
                    .status(ChangePasswordResult.Status.ACCOUNT_NOT_FOUND)
                    .message("계정을 찾을 수 없습니다.")
                    .build();
        }

        BackofficeAdmin account = accountOpt.get();

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            return ChangePasswordResult.builder()
                    .status(ChangePasswordResult.Status.CURRENT_PASSWORD_MISMATCH)
                    .message("현재 비밀번호가 일치하지 않습니다.")
                    .build();
        }

        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            return ChangePasswordResult.builder()
                    .status(ChangePasswordResult.Status.SAME_AS_CURRENT)
                    .message("새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.")
                    .build();
        }

        account.changePassword(passwordEncoder.encode(newPassword));

        return ChangePasswordResult.builder()
                .status(ChangePasswordResult.Status.SUCCESS)
                .build();
    }
}