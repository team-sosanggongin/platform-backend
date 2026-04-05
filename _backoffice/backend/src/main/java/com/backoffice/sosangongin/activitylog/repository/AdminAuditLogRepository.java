package com.backoffice.sosangongin.activitylog.repository;

import com.backoffice.sosangongin.activitylog.domain.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    List<AdminAuditLog> findAllByOrderByCreatedAtDesc();
    List<AdminAuditLog> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}