package com.platform.sosangongin.cases.app;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CheckAppStatusResult {

    private final boolean serviceAvailable;
    private final MaintenanceInfo maintenance;
    private final VersionInfo version;

    @Builder
    public CheckAppStatusResult(boolean serviceAvailable,
                                MaintenanceInfo maintenance,
                                VersionInfo version) {
        this.serviceAvailable = serviceAvailable;
        this.maintenance = maintenance;
        this.version = version;
    }

    @Builder
    @Getter
    public static class MaintenanceInfo {
        private final boolean active;
        private final String reason;
        private final LocalDateTime startedAt;
        private final LocalDateTime endedAt;
    }

    @Builder
    @Getter
    public static class VersionInfo {
        private final String latestVersion;
        private final int latestVersionCode;
        private final boolean supported;
        private final boolean forceUpdateRequired;
    }
}
