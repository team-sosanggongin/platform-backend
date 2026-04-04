package com.backoffice.sosangongin.global.aop;

import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import com.backoffice.sosangongin.global.exception.PermissionDeniedException;
import com.backoffice.sosangongin.role.repository.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final SessionManager sessionManager;
    private final AccountRoleRepository accountRoleRepository;

    @Around("@annotation(requiresPermission)")
    public Object check(ProceedingJoinPoint joinPoint,
                        RequiresPermission requiresPermission) throws Throwable {
        UUID accountId = sessionManager.getAccountId()
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));

        if (SessionManager.ROLE_ROOT.equals(sessionManager.getRole())) {
            return joinPoint.proceed();
        }

        String requiredPermission = requiresPermission.value();

        boolean hasPermission = accountRoleRepository.findByIdAccountId(accountId)
                .stream()
                .flatMap(ar -> ar.getRole().getRolePermissions().stream())
                .anyMatch(rp -> rp.getPermission().getPermissionName().equals(requiredPermission));

        if (!hasPermission) {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
        return joinPoint.proceed();
    }
}