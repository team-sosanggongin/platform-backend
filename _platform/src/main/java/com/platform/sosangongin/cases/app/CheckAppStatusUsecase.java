package com.platform.sosangongin.cases.app;

import com.platform.sosangongin.domains.app.*;
import com.platform.sosangongin.domains.app.AppMaintenanceRepository;
import com.platform.sosangongin.domains.app.AppVersionRepository;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckAppStatusUsecase {

    private final AppMaintenanceRepository maintenanceRepository;
    private final AppVersionRepository versionRepository;
    private final TimeGeneratorService timeGeneratorService;

    public CheckAppStatusResult check(CheckAppStatusRequest request) {
        LocalDateTime now = timeGeneratorService.now();

        CheckAppStatusResult.MaintenanceInfo maintenanceInfo = checkMaintenance(now);
        CheckAppStatusResult.VersionInfo versionInfo = checkVersion(request);

        boolean serviceAvailable = !maintenanceInfo.isActive()
                && (versionInfo == null || versionInfo.isSupported());

        return CheckAppStatusResult.builder()
                .httpStatus(HttpStatus.OK)
                .serviceAvailable(serviceAvailable)
                .maintenance(maintenanceInfo)
                .version(versionInfo)
                .build();
    }

    private CheckAppStatusResult.MaintenanceInfo checkMaintenance(LocalDateTime now) {
        return maintenanceRepository.findActiveAt(now)
                .map(m -> CheckAppStatusResult.MaintenanceInfo.builder()
                        .active(true)
                        .reason(m.getReason())
                        .startedAt(m.getStartedAt())
                        .endedAt(m.getEndedAt())
                        .build())
                .orElse(CheckAppStatusResult.MaintenanceInfo.builder()
                        .active(false)
                        .build());
    }

    private CheckAppStatusResult.VersionInfo checkVersion(CheckAppStatusRequest request) {
        Optional<AppVersion> latestVersion = versionRepository
                .findTopByPlatformOrderByVersionCodeDesc(request.getPlatform());

        if (latestVersion.isEmpty()) {
            return null;
        }

        boolean supported = versionRepository
                .existsByPlatformAndMinSupportedVersionCodeLessThanEqual(
                        request.getPlatform(), request.getVersionCode());

        AppVersion v = latestVersion.get();
        return CheckAppStatusResult.VersionInfo.builder()
                .latestVersion(v.getVersionName())
                .latestVersionCode(v.getVersionCode())
                .supported(supported)
                .forceUpdateRequired(!supported && v.isForceUpdate())
                .build();
    }
}
