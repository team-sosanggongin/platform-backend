package com.backoffice.sosangongin.notice.dto;

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
    private boolean isServiceMaintenance;
}