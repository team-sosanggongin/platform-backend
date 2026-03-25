package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.domains.loginHistory.AdminLoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginUsecase {
    private final BackofficeAdminRepository backofficeAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminLoginHistoryService adminLoginHistoryService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    @Transactional
    public LoginResult login(String loginId, String rawPassword, HttpServletRequest request, HttpSession session) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Optional<BackofficeAdmin> accountOpt = backofficeAdminRepository.findByLoginId(loginId);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        BackofficeAdmin account = accountOpt.get();

        try {
            if (account.isLocked()) {
                throw new IllegalStateException("계정이 5회 이상 로그인 실패하여 잠겼습니다. 관리자에게 문의하세요.");
            }

            if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
                account.incrementFailedLoginAttempts();
                if (account.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                    account.lockAccount();
                }
                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
            }

            account.resetFailedLoginAttempts();
            session.setAttribute("ACCOUNT_ID", account.getId());

            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, true);

            return LoginResult.builder()
                    .passwordExpired(account.isPasswordExpired())
                    .build();

        } catch (Exception e) {
            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, false);
            throw e;
        }
    }
}