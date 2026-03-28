package com.backoffice.sosangongin.cases.notice;

import com.backoffice.sosangongin.domains.notice.NoticeContent;
import com.backoffice.sosangongin.domains.notice.NoticeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateNoticeCommand {
    private final String title;
    private final String content;
    private final String author;
    private final NoticeStatus status;
    private final boolean pinned;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDateTime scheduledAt;
    private final boolean systemMaintenance;
    private final LocalDateTime maintenanceStartAt;
    private final LocalDateTime maintenanceEndAt;

    public NoticeContent toNoticeContent() {
        return NoticeContent.builder()
                .title(title)
                .content(content)
                .authorName(author)
                .status(status)
                .isPinned(pinned)
                .startsAt(startAt)
                .endsAt(endAt)
                .scheduledAt(scheduledAt)
                .isSystemMaintenance(systemMaintenance)
                .maintenanceStartAt(maintenanceStartAt)
                .maintenanceEndAt(maintenanceEndAt)
                .build();
    }
}
