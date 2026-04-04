package com.backoffice.sosangongin.notice.domain;

import com.backoffice.sosangongin.global.entity.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "public_notice")
public class Notice extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "created_by")
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NoticeStatus status = NoticeStatus.DRAFT;

    @Builder.Default
    @Column(name = "is_pinned")
    private boolean isPinned = false;

    @Builder.Default
    @Column(name = "is_maintenance")
    private boolean isServiceMaintenance = false;

    @Column(name = "view_count", insertable = false, updatable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    public void update(String title, String content, LocalDateTime startsAt, LocalDateTime endsAt, boolean isServiceMaintenance) {
        this.title = title;
        this.content = content;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.isServiceMaintenance = isServiceMaintenance;
    }

    public void changeStatus(NoticeStatus status) {
        this.status = status;
    }
}