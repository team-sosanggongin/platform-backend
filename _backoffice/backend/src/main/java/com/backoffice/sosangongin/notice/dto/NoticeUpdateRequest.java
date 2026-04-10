package com.backoffice.sosangongin.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeUpdateRequest {
    private String title;
    private String content;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @JsonProperty("isServiceMaintenance")
    private boolean isServiceMaintenance;

    private LocalDateTime scheduledAt;
    private LocalDateTime maintenanceStartAt;
    private LocalDateTime maintenanceEndAt;
}