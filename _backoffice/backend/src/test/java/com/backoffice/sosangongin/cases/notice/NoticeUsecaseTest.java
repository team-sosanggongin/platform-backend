package com.backoffice.sosangongin.cases.notice;

import com.backoffice.sosangongin.domains.notice.BackofficeNotice;
import com.backoffice.sosangongin.domains.notice.BackofficeNoticeRepository;
import com.backoffice.sosangongin.domains.notice.NoticeStatus;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NoticeUsecaseTest {

    @Autowired
    private NoticeUsecase noticeUsecase;

    @Autowired
    private BackofficeNoticeRepository noticeRepository;

    @BeforeEach
    void setup() {
        noticeRepository.deleteAll();
    }

    @Test
    @DisplayName("공지 생성: DRAFT 상태로 생성되면 publishedAt은 null이다")
    void createNotice_draft() {
        CreateNoticeCommand command = CreateNoticeCommand.builder()
                .title("테스트 공지")
                .content("내용입니다")
                .author("관리자")
                .status(NoticeStatus.DRAFT)
                .startAt(LocalDateTime.now())
                .build();

        BackofficeNotice result = noticeUsecase.createNotice(command);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 공지");
        assertThat(result.getAuthorName()).isEqualTo("관리자");
        assertThat(result.getStatus()).isEqualTo(NoticeStatus.DRAFT);
        assertThat(result.getPublishedAt()).isNull();
    }

    @Test
    @DisplayName("공지 생성: PUBLISHED 상태로 생성되면 publishedAt이 자동 세팅된다")
    void createNotice_published_setsPublishedAt() {
        CreateNoticeCommand command = CreateNoticeCommand.builder()
                .title("발행 공지")
                .content("발행 내용")
                .author("관리자")
                .status(NoticeStatus.PUBLISHED)
                .startAt(LocalDateTime.now())
                .build();

        BackofficeNotice result = noticeUsecase.createNotice(command);

        assertThat(result.getStatus()).isEqualTo(NoticeStatus.PUBLISHED);
        assertThat(result.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("공지 생성: 시스템 점검 공지를 생성할 수 있다")
    void createNotice_systemMaintenance() {
        LocalDateTime maintenanceStart = LocalDateTime.now().plusDays(1);
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(1).plusHours(2);

        CreateNoticeCommand command = CreateNoticeCommand.builder()
                .title("시스템 점검 안내")
                .content("점검 내용")
                .author("관리자")
                .status(NoticeStatus.PUBLISHED)
                .startAt(LocalDateTime.now())
                .systemMaintenance(true)
                .maintenanceStartAt(maintenanceStart)
                .maintenanceEndAt(maintenanceEnd)
                .build();

        BackofficeNotice result = noticeUsecase.createNotice(command);

        assertThat(result.isSystemMaintenance()).isTrue();
        assertThat(result.getMaintenanceStartAt()).isEqualTo(maintenanceStart);
        assertThat(result.getMaintenanceEndAt()).isEqualTo(maintenanceEnd);
    }

    @Test
    @DisplayName("공지 단건 조회: 존재하는 공지를 조회한다")
    void getNotice_success() {
        BackofficeNotice saved = createTestNotice("조회 테스트");

        BackofficeNotice result = noticeUsecase.getNotice(saved.getId());

        assertThat(result.getTitle()).isEqualTo("조회 테스트");
    }

    @Test
    @DisplayName("공지 단건 조회: 존재하지 않으면 EntityNotFoundException")
    void getNotice_notFound() {
        assertThatThrownBy(() -> noticeUsecase.getNotice(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("공지 단건 조회: 삭제된 공지는 조회되지 않는다")
    void getNotice_deletedNotFound() {
        BackofficeNotice saved = createTestNotice("삭제 테스트");
        saved.delete();
        noticeRepository.save(saved);

        assertThatThrownBy(() -> noticeUsecase.getNotice(saved.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("공지 목록 조회: 삭제되지 않은 공지만 최신순으로 반환한다")
    void getAllNotices_excludesDeleted() {
        createTestNotice("공지1");
        createTestNotice("공지2");
        BackofficeNotice deleted = createTestNotice("삭제됨");
        deleted.delete();
        noticeRepository.save(deleted);

        List<BackofficeNotice> result = noticeUsecase.getAllNotices();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("공지 수정: SCHEDULED → PUBLISHED 변경 시 publishedAt이 자동 세팅된다")
    void updateNotice_scheduledToPublished_setsPublishedAt() {
        BackofficeNotice saved = noticeRepository.save(BackofficeNotice.builder()
                .title("예약 공지")
                .content("내용")
                .authorName("관리자")
                .status(NoticeStatus.SCHEDULED)
                .startsAt(LocalDateTime.now().plusDays(1))
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .build());

        assertThat(saved.getPublishedAt()).isNull();

        UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .title("발행된 공지")
                .content("내용")
                .author("관리자")
                .status(NoticeStatus.PUBLISHED)
                .startAt(LocalDateTime.now())
                .build();

        BackofficeNotice result = noticeUsecase.updateNotice(saved.getId(), command);

        assertThat(result.getStatus()).isEqualTo(NoticeStatus.PUBLISHED);
        assertThat(result.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("공지 수정: 이미 PUBLISHED인 공지의 publishedAt은 유지된다")
    void updateNotice_alreadyPublished_keepsPublishedAt() {
        LocalDateTime originalPublishedAt = LocalDateTime.of(2026, 1, 1, 12, 0);
        BackofficeNotice saved = noticeRepository.save(BackofficeNotice.builder()
                .title("기존 공지")
                .content("내용")
                .authorName("관리자")
                .status(NoticeStatus.PUBLISHED)
                .startsAt(LocalDateTime.now())
                .publishedAt(originalPublishedAt)
                .build());

        UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .title("수정된 공지")
                .content("수정 내용")
                .author("관리자")
                .status(NoticeStatus.PUBLISHED)
                .startAt(LocalDateTime.now())
                .build();

        BackofficeNotice result = noticeUsecase.updateNotice(saved.getId(), command);

        assertThat(result.getPublishedAt()).isEqualTo(originalPublishedAt);
    }

    @Test
    @DisplayName("공지 삭제: soft delete 처리된다")
    void deleteNotice_softDeletes() {
        BackofficeNotice saved = createTestNotice("삭제 대상");

        noticeUsecase.deleteNotice(saved.getId());

        BackofficeNotice deleted = noticeRepository.findById(saved.getId()).get();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("공지 삭제: 존재하지 않는 공지는 EntityNotFoundException")
    void deleteNotice_notFound() {
        assertThatThrownBy(() -> noticeUsecase.deleteNotice(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private BackofficeNotice createTestNotice(String title) {
        return noticeRepository.save(BackofficeNotice.builder()
                .title(title)
                .content("테스트 내용")
                .authorName("관리자")
                .status(NoticeStatus.DRAFT)
                .startsAt(LocalDateTime.now())
                .build());
    }
}