package com.backoffice.sosangongin.activitylog.repository;

import com.backoffice.sosangongin.activitylog.domain.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    Page<AdminAuditLog> findByAccountId(UUID accountId, Pageable pageable);
}
