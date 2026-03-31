package com.platform.sosangongin.cases.app.version;

import com.platform.sosangongin.domains.app.version.AppVersion;
import com.platform.sosangongin.domains.app.version.AppVersionRepository;
import com.platform.sosangongin.domains.app.version.CompatibleCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckAppVersionUsecase {

    private final AppVersionRepository versionRepository;

    public CheckAppVersionResult check(CheckAppVersionRequest request) {
        Optional<AppVersion> latestVersionOpt = versionRepository
                .findTopByPlatformOrderByCreatedAtDesc(request.getPlatform());

        if (latestVersionOpt.isEmpty()) {
            log.warn("No version info found for platform: {}", request.getPlatform());
            return CheckAppVersionResult.builder()
                    .compatible(true)
                    .forceUpdateRequired(false)
                    .build();
        }

        AppVersion latestVersion = latestVersionOpt.get();
        CompatibleCheck compatibleCheck = latestVersion.check(request.getPlatform(), request.getAppVersion());

        return CheckAppVersionResult.builder()
                .compatible(compatibleCheck.isOk())
                .forceUpdateRequired(!compatibleCheck.isOk() && latestVersion.isForceUpdate())
                .latestVersion(latestVersion.getVersionSpan().getVersionCode())
                .reason(compatibleCheck.getReason())
                .build();
    }
}
