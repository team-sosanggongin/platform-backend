package com.platform.sosangongin.api.controllers.app;

import com.platform.sosangongin.cases.app.status.CheckAppStatusResult;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CheckAppStatusApiResponse {
    private final boolean underMaintenance;
    private final String reason;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;

    public CheckAppStatusApiResponse(CheckAppStatusResult result) {
        this.underMaintenance = result.isUnderMaintenance();
        this.reason = result.getReason();
        this.startedAt = result.getStartedAt();
        this.endedAt = result.getEndedAt();
    }
}
