package com.backoffice.sosangongin.controller.dto.notice;

import com.backoffice.sosangongin.domains.notice.BackofficeNotice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final String status;
    private final boolean isPinned;
    private final Long viewCount;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime publishedAt;
    private final boolean isSystemMaintenance;
    private final LocalDateTime maintenanceStartAt;
    private final LocalDateTime maintenanceEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static NoticeResponse from(BackofficeNotice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthorName())
                .status(notice.getStatus().name().toLowerCase())
                .isPinned(notice.isPinned())
                .viewCount(notice.getViewCount())
                .startAt(notice.getStartsAt())
                .endAt(notice.getEndsAt())
                .scheduledAt(notice.getScheduledAt())
                .publishedAt(notice.getPublishedAt())
                .isSystemMaintenance(notice.isSystemMaintenance())
                .maintenanceStartAt(notice.getMaintenanceStartAt())
                .maintenanceEndAt(notice.getMaintenanceEndAt())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}