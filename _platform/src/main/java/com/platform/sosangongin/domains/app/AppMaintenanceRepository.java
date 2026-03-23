package com.platform.sosangongin.domains.app;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppMaintenanceRepository extends JpaRepository<AppMaintenance, UUID> {

    List<AppMaintenance> findByStatusIn(List<MaintenanceStatus> statuses);

    List<AppMaintenance> findByStatusAndEndedAtAfter(MaintenanceStatus status, LocalDateTime now);
}
