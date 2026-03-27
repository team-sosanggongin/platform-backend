package com.platform.sosangongin.cases.app.status;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CheckAppStatusResult {
    private final boolean underMaintenance;
    private final String reason;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
}