package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.app.CheckAppStatusResult;
import lombok.Getter;

@Getter
public class CheckAppStatusApiResponse extends CommonResultTemplate {
    private final boolean serviceAvailable;
    private final CheckAppStatusResult.MaintenanceInfo maintenance;
    private final CheckAppStatusResult.VersionInfo version;

    public CheckAppStatusApiResponse(CheckAppStatusResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.serviceAvailable = result.isServiceAvailable();
        this.maintenance = result.getMaintenance();
        this.version = result.getVersion();
    }
}
