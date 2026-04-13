package com.backoffice.sosangongin.global.aop;

import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import com.backoffice.sosangongin.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequiresRootAspect {

    private final SessionManager sessionManager;

    @Around("@annotation(com.backoffice.sosangongin.global.aop.RequiresRoot)")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        sessionManager.getAccountId()
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));

        if (!SessionManager.ROLE_ROOT.equals(sessionManager.getRole())) {
            throw new PermissionDeniedException("ROOT 권한이 필요합니다.");
        }

        return joinPoint.proceed();
    }
}
