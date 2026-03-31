package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.domains.loginHistory.AdminLoginHistoryService;
import com.backoffice.sosangongin.errors.AccountLockedException;
import com.backoffice.sosangongin.errors.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUsecase {
    private final BackofficeAdminRepository backofficeAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminLoginHistoryService adminLoginHistoryService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    @Transactional(noRollbackFor = {InvalidCredentialsException.class, AccountLockedException.class})
    public BackofficeAdmin login(String loginId, String rawPassword, String ipAddress, String userAgent) {
        BackofficeAdmin account = backofficeAdminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 잘못되었습니다."));

        if (account.isLocked()) {
            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, false);
            throw new AccountLockedException("계정이 잠겼습니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            account.incrementFailedLoginAttempts();
            if (account.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                account.lockAccount();
            }
            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, false);
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        account.resetFailedLoginAttempts();
        adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, true);

        return account;
    }
}
