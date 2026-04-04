package com.backoffice.sosangongin.global.aop;

import com.backoffice.sosangongin.global.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requiresPermission)")
    public Object check(ProceedingJoinPoint joinPoint,
                        RequiresPermission requiresPermission) throws Throwable {
        // TODO: notice 브랜치에서 실제 permission 체크 로직 구현
        // 1. SecurityContext에서 accountId 꺼내기
        // 2. accountId로 role 조회
        // 3. role에 매핑된 permission 목록 조회
        // 4. requiresPermission.value()와 매칭 여부 확인
        // 5. (예외 처리) PermissionDeniedException

        log.debug("Permission check: {}", requiresPermission.value());
        return joinPoint.proceed();
    }
}