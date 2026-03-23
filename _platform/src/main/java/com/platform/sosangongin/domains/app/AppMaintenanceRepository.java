package com.platform.sosangongin.domains.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AppMaintenanceRepository extends JpaRepository<AppMaintenance, UUID> {

    @Query("SELECT m FROM AppMaintenance m " +
            "WHERE m.startedAt <= :now AND m.endedAt > :now " +
            "AND m.status IN ('SCHEDULED', 'IN_PROGRESS') " +
            "ORDER BY m.startedAt DESC " +
            "LIMIT 1")
    Optional<AppMaintenance> findActiveAt(@Param("now") LocalDateTime now);
}
