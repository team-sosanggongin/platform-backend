package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.app.CheckAppStatusResult;
import lombok.Getter;

@Getter
public class CheckAppStatusApiResponse {
    private final boolean serviceAvailable;
    private final CheckAppStatusResult.MaintenanceInfo maintenance;
    private final CheckAppStatusResult.VersionInfo version;

    public CheckAppStatusApiResponse(CheckAppStatusResult result) {
        this.serviceAvailable = result.isServiceAvailable();
        this.maintenance = result.getMaintenance();
        this.version = result.getVersion();
    }
}
