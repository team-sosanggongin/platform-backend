package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.domains.notices.NoticeStatus;
import com.platform.sosangongin.domains.notices.PublicNotice;

import java.time.LocalDateTime;

public record PublicNoticeVo (
        Long id,
        String title,
        String content,
        String authorName,
        NoticeStatus status,
        boolean isPinned,
        Long viewCount,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 엔티티를 VO로 변환하는 정적 팩토리 메서드
    public static PublicNoticeVo from(PublicNotice notice) {
        return new PublicNoticeVo(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorName(),
                notice.getStatus(),
                notice.isPinned(),
                notice.getViewCount(),
                notice.getStartsAt(),
                notice.getEndsAt(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
