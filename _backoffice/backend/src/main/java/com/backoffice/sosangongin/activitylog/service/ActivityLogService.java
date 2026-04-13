package com.backoffice.sosangongin.activitylog.service;

import com.backoffice.sosangongin.activitylog.domain.ActionType;
import com.backoffice.sosangongin.activitylog.domain.AdminAuditLog;
import com.backoffice.sosangongin.activitylog.domain.AdminLoginHistory;
import com.backoffice.sosangongin.activitylog.domain.ResourceDomain;
import com.backoffice.sosangongin.activitylog.repository.AdminAuditLogRepository;
import com.backoffice.sosangongin.activitylog.repository.AdminLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final AdminLoginHistoryRepository loginHistoryRepository;
    private final AdminAuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLoginHistory(UUID accountId, String loginId, String ipAddress, String userAgent, boolean isSuccess) {
        loginHistoryRepository.save(
                AdminLoginHistory.builder()
                        .accountId(accountId)
                        .loginId(loginId)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .isSuccess(isSuccess)
                        .build()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(UUID accountId, ActionType actionType, ResourceDomain resourceDomain, String resourceId) {
        auditLogRepository.save(
                AdminAuditLog.builder()
                        .accountId(accountId)
                        .actionType(actionType)
                        .resourceDomain(resourceDomain)
                        .resourceId(resourceId)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> findAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> findAuditLogsByAccountId(UUID accountId, Pageable pageable) {
        return auditLogRepository.findByAccountId(accountId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminLoginHistory> findAllLoginHistories(Pageable pageable) {
        return loginHistoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminLoginHistory> findLoginHistoriesByAccountId(UUID accountId, Pageable pageable) {
        return loginHistoryRepository.findByAccountId(accountId, pageable);
    }
}
