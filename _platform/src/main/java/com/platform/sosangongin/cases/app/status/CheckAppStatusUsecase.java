package com.platform.sosangongin.cases.app.status;

import com.platform.sosangongin.domains.app.maintenance.AppMaintenance;
import com.platform.sosangongin.domains.app.maintenance.AppMaintenanceRepository;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckAppStatusUsecase {

    private final AppMaintenanceRepository maintenanceRepository;
    private final TimeGeneratorService timeGeneratorService;

    public CheckAppStatusResult check() {
        LocalDateTime now = timeGeneratorService.now();
        Optional<AppMaintenance> activeMaintenanceOpt = maintenanceRepository.findActiveAt(now);

        if (activeMaintenanceOpt.isEmpty()) {
            return CheckAppStatusResult.builder()
                    .underMaintenance(false)
                    .build();
        }

        AppMaintenance maintenance = activeMaintenanceOpt.get();
        return CheckAppStatusResult.builder()
                .underMaintenance(true)
                .reason(maintenance.getReason())
                .startedAt(maintenance.getStartedAt())
                .endedAt(maintenance.getEndedAt())
                .build();
    }
}
