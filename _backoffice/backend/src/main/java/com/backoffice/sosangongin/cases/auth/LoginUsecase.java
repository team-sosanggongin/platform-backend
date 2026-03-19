package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.AccountBackoffice;
import com.backoffice.sosangongin.domains.account.AccountBackofficeRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUsecase {
    private final AccountBackofficeRepository accountBackofficeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    @Transactional
    public void login(String loginId, String rawPassword, HttpSession session) {
        AccountBackoffice account = accountBackofficeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));

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
    }
}
