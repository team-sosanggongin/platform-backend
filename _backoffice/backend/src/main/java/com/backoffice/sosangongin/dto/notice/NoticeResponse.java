package com.backoffice.sosangongin.dto.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NoticeResponse {
    private final UUID id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
}
