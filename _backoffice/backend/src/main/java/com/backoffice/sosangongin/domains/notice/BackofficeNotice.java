package com.backoffice.sosangongin.domains.notice;

import com.backoffice.sosangongin.domains.common.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "public_notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BackofficeNotice extends SoftDeletedBaseEntity {

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

    @Column(nullable = false)
    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private LocalDateTime scheduledAt;

    private LocalDateTime publishedAt;

    @Builder.Default
    private boolean isSystemMaintenance = false;

    private LocalDateTime maintenanceStartAt;

    private LocalDateTime maintenanceEndAt;

    public static BackofficeNotice create(NoticeContent noticeContent) {
        BackofficeNotice notice = BackofficeNotice.builder()
                .title(noticeContent.getTitle())
                .content(noticeContent.getContent())
                .authorName(noticeContent.getAuthorName())
                .status(noticeContent.getStatus())
                .isPinned(noticeContent.isPinned())
                .startsAt(noticeContent.getStartsAt())
                .endsAt(noticeContent.getEndsAt())
                .scheduledAt(noticeContent.getScheduledAt())
                .isSystemMaintenance(noticeContent.isSystemMaintenance())
                .maintenanceStartAt(noticeContent.getMaintenanceStartAt())
                .maintenanceEndAt(noticeContent.getMaintenanceEndAt())
                .build();

        if (notice.status == NoticeStatus.PUBLISHED) {
            notice.publishedAt = LocalDateTime.now();
        }

        return notice;
    }

    public void update(NoticeContent noticeContent) {
        this.title = noticeContent.getTitle();
        this.content = noticeContent.getContent();
        this.authorName = noticeContent.getAuthorName();
        this.status = noticeContent.getStatus();
        this.isPinned = noticeContent.isPinned();
        this.startsAt = noticeContent.getStartsAt();
        this.endsAt = noticeContent.getEndsAt();
        this.scheduledAt = noticeContent.getScheduledAt();
        this.isSystemMaintenance = noticeContent.isSystemMaintenance();
        this.maintenanceStartAt = noticeContent.getMaintenanceStartAt();
        this.maintenanceEndAt = noticeContent.getMaintenanceEndAt();

        if (this.status == NoticeStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
}
