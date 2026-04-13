package com.backoffice.sosangongin.global.aop;

import com.backoffice.sosangongin.activitylog.service.ActivityLogService;
import com.backoffice.sosangongin.auth.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final ActivityLogService activityLogService;
    private final SessionManager sessionManager;

    @Around("@annotation(auditLog)")
    public Object log(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            UUID accountId = sessionManager.getAccountId().orElse(null);
            String resourceId = extractResourceId(joinPoint, auditLog, result);
            activityLogService.saveAuditLog(accountId, auditLog.action(), auditLog.domain(), resourceId);
        } catch (Exception e) {
            log.warn("[AuditLog] 활동 이력 저장 실패: {}", e.getMessage());
        }

        return result;
    }

    private String extractResourceId(ProceedingJoinPoint joinPoint, AuditLog auditLog, Object result) {
        if (!auditLog.resourceIdParam().isEmpty()) {
            return extractFromParam(joinPoint, auditLog.resourceIdParam());
        }
        return extractFromResult(result);
    }

    private String extractFromParam(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                return args[i] != null ? args[i].toString() : null;
            }
        }
        return null;
    }

    private String extractFromResult(Object result) {
        if (result == null) return null;
        try {
            Object target = result;
            if (result instanceof ResponseEntity<?> responseEntity) {
                target = responseEntity.getBody();
            }
            if (target == null) return null;
            return target.getClass().getMethod("getId").invoke(target).toString();
        } catch (Exception e) {
            return null;
        }
    }
}
