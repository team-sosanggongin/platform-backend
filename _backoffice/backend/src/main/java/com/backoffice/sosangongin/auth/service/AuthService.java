package com.backoffice.sosangongin.auth.service;

import com.backoffice.sosangongin.activitylog.service.ActivityLogService;
import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.auth.repos.AdminRepository;
import com.backoffice.sosangongin.global.exception.AccountLockedException;
import com.backoffice.sosangongin.global.exception.EntityNotFoundException;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final ActivityLogService activityLogService;

    @Transactional
    public BackofficeAdmin authenticate(String loginId, String password, String ipAddress, String userAgent) {
        BackofficeAdmin admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계정입니다."));

        if (admin.isLocked()) {
            activityLogService.saveLoginHistory(admin.getId(), ipAddress, userAgent, false);
            throw new AccountLockedException("잠긴 계정입니다.");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            loginAttemptService.handleFailedAttempt(admin);
            activityLogService.saveLoginHistory(admin.getId(), ipAddress, userAgent, false);
            throw new InvalidCredentialsException("비밀번호가 올바르지 않습니다.");
        }

        admin.resetFailedAttempts();
        activityLogService.saveLoginHistory(admin.getId(), ipAddress, userAgent, true);
        return admin;
    }
}