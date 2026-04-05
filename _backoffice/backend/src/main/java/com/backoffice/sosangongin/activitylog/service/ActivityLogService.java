package com.backoffice.sosangongin.activitylog.service;

import com.backoffice.sosangongin.activitylog.domain.ActionType;
import com.backoffice.sosangongin.activitylog.domain.AdminAuditLog;
import com.backoffice.sosangongin.activitylog.domain.AdminLoginHistory;
import com.backoffice.sosangongin.activitylog.domain.ResourceDomain;
import com.backoffice.sosangongin.activitylog.repository.AdminAuditLogRepository;
import com.backoffice.sosangongin.activitylog.repository.AdminLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final AdminLoginHistoryRepository loginHistoryRepository;
    private final AdminAuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLoginHistory(UUID accountId, String ipAddress, String userAgent, boolean isSuccess) {
        loginHistoryRepository.save(
                AdminLoginHistory.builder()
                        .accountId(accountId)
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
    public List<AdminAuditLog> findAllAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AdminAuditLog> findAuditLogsByAccountId(UUID accountId) {
        return auditLogRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @Transactional(readOnly = true)
    public List<AdminLoginHistory> findAllLoginHistories() {
        return loginHistoryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AdminLoginHistory> findLoginHistoriesByAccountId(UUID accountId) {
        return loginHistoryRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}
