package com.backoffice.sosangongin.domains.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeContent {
    private final String title;
    private final String content;
    private final String authorName;
    private final NoticeStatus status;
    private final boolean isPinned;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final LocalDateTime scheduledAt;
    private final boolean isSystemMaintenance;
    private final LocalDateTime maintenanceStartAt;
    private final LocalDateTime maintenanceEndAt;
}
