package com.platform.sosangongin.domains.notices;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PublicNotice extends SoftDeletedBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String authorName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NoticeStatus status = NoticeStatus.DRAFT;

    @Builder.Default
    private boolean isPinned = false;

    @Builder.Default
    private Long viewCount = 0L;

    // 공지 시작 및 예약 시간
    @Column(nullable = false)
    private LocalDateTime startsAt;

    // 공지 종료 시간 (null이면 영구 게시)
    private LocalDateTime endsAt;

    // 조회수 증가 도메인 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean isDisplayable(LocalDateTime now) {
        return this.status == NoticeStatus.PUBLISHED &&
                this.startsAt.isBefore(now) &&
                (this.endsAt == null || this.endsAt.isAfter(now));
    }
}
