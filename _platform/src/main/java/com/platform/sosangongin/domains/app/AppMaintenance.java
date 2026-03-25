package com.platform.sosangongin.domains.app;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "app_maintenances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppMaintenance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MaintenanceStatus status;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    public boolean isActive(LocalDateTime now) {
        return (status == MaintenanceStatus.SCHEDULED || status == MaintenanceStatus.IN_PROGRESS)
                && !now.isBefore(startedAt) && now.isBefore(endedAt);
    }

    public void start() {
        this.status = MaintenanceStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = MaintenanceStatus.COMPLETED;
    }
}
