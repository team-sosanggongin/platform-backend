package com.backoffice.sosangongin.notice.dto;

import com.backoffice.sosangongin.notice.domain.Notice;
import com.backoffice.sosangongin.notice.domain.NoticeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private UUID createdBy;
    private NoticeStatus status;
    private boolean isPinned;
    private boolean isServiceMaintenance;
    private Long viewCount;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private LocalDateTime createdAt;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorName(notice.getAuthorName())
                .createdBy(notice.getCreatedBy())
                .status(notice.getStatus())
                .isPinned(notice.isPinned())
                .isServiceMaintenance(notice.isServiceMaintenance())
                .viewCount(notice.getViewCount())
                .startsAt(notice.getStartsAt())
                .endsAt(notice.getEndsAt())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}