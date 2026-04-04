package com.backoffice.sosangongin.notice.dto;

import com.backoffice.sosangongin.notice.domain.NoticeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeStatusRequest {
    private NoticeStatus status;
}