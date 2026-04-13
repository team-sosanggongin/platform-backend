package com.backoffice.sosangongin.activitylog.repository;

import com.backoffice.sosangongin.activitylog.domain.AdminLoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long> {
    Page<AdminLoginHistory> findByAccountId(UUID accountId, Pageable pageable);
}
