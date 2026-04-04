package com.backoffice.sosangongin.auth.service;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.auth.repos.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private final AdminRepository adminRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 핵심 - 별도 트랜잭션
    public void handleFailedAttempt(BackofficeAdmin admin) {
        admin.increaseFailedAttempts();
        if (admin.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            admin.lock();
        }
        adminRepository.saveAndFlush(admin);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSuccess(BackofficeAdmin admin) {
        admin.resetFailedAttempts();
        adminRepository.saveAndFlush(admin);
    }
}