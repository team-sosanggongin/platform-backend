package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.AccountBackoffice;
import com.backoffice.sosangongin.domains.account.AccountBackofficeRepository;
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
    private final AccountBackofficeRepository accountBackofficeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminLoginHistoryService adminLoginHistoryService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    @Transactional
    public void login(String loginId, String rawPassword, HttpServletRequest request, HttpSession session) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Optional<AccountBackoffice> accountOpt = accountBackofficeRepository.findByLoginId(loginId);

        if (accountOpt.isEmpty()) {
            // ID가 존재하지 않아도 실패 이력을 남기기 위해 임의의 UUID를 사용할 수 있으나, 여기서는 null로 처리.
            // 또는 별도의 'unknown_user' 같은 ID를 사용할 수도 있음. 요구사항에 따라 달라짐.
            // 여기서는 이력을 남기지 않거나, 남기더라도 accountId 없이 남기는 것을 선택할 수 있음.
            // 이번 구현에서는 존재하지 않는 계정에 대한 이력은 남기지 않고 바로 예외를 던짐.
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        AccountBackoffice account = accountOpt.get();

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

            // 로그인 성공 이력 기록
            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, true);

        } catch (Exception e) {
            // 로그인 실패 이력 기록
            adminLoginHistoryService.recordLoginAttempt(account.getId(), ipAddress, userAgent, false);
            // 발생한 예외를 다시 던져서 GlobalExceptionHandler가 처리하도록 함
            throw e;
        }
    }
}
