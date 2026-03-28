package com.backoffice.sosangongin.controller.dto.notice;

import com.backoffice.sosangongin.cases.notice.CreateNoticeCommand;
import com.backoffice.sosangongin.domains.notice.NoticeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateNoticeRequest {
    private String title;
    private String content;
    private String author;
    private String status;
    private boolean isPinned;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime scheduledAt;
    private boolean isSystemMaintenance;
    private LocalDateTime maintenanceStartAt;
    private LocalDateTime maintenanceEndAt;

    public CreateNoticeCommand toCommand() {
        return CreateNoticeCommand.builder()
                .title(title)
                .content(content)
                .author(author)
                .status(NoticeStatus.valueOf(status.toUpperCase()))
                .pinned(isPinned)
                .startAt(startAt)
                .endAt(endAt)
                .scheduledAt(scheduledAt)
                .systemMaintenance(isSystemMaintenance)
                .maintenanceStartAt(maintenanceStartAt)
                .maintenanceEndAt(maintenanceEndAt)
                .build();
    }
}