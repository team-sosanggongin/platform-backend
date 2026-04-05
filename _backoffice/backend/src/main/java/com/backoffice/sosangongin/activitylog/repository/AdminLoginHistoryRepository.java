package com.backoffice.sosangongin.activitylog.repository;

import com.backoffice.sosangongin.activitylog.domain.AdminLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long> {
    List<AdminLoginHistory> findAllByOrderByCreatedAtDesc();
    List<AdminLoginHistory> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}