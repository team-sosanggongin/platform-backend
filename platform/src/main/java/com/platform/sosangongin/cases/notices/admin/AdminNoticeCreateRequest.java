package com.platform.sosangongin.cases.notices.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AdminNoticeCreateRequest {
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime startsAt; // 게시 시작일
    private LocalDateTime endsAt;   // 게시 종료일
    private boolean isPinned;   // pin 여부
    private boolean sendPush;   // 푸시 알림 발송 여부 선택하기
}
