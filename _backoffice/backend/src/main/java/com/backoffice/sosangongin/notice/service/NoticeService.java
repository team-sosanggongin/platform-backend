package com.backoffice.sosangongin.notice.service;

import com.backoffice.sosangongin.global.exception.EntityNotFoundException;
import com.backoffice.sosangongin.global.exception.ValidationFailedException;
import com.backoffice.sosangongin.notice.domain.Notice;
import com.backoffice.sosangongin.notice.domain.NoticeStatus;
import com.backoffice.sosangongin.notice.dto.NoticeCreateRequest;
import com.backoffice.sosangongin.notice.dto.NoticeUpdateRequest;
import com.backoffice.sosangongin.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public Notice create(NoticeCreateRequest request, UUID createdBy, String authorName) {
        return noticeRepository.save(
                Notice.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .authorName(authorName)
                        .createdBy(createdBy)
                        .startsAt(request.getStartsAt())
                        .endsAt(request.getEndsAt())
                        .isServiceMaintenance(request.isServiceMaintenance())
                        .scheduledAt(request.getScheduledAt())
                        .maintenanceStartAt(request.getMaintenanceStartAt())
                        .maintenanceEndAt(request.getMaintenanceEndAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Notice> findAll() {
        return noticeRepository.findByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public Page<Notice> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return noticeRepository.findByDeletedAtIsNull(pageable);
        }
        return noticeRepository.searchByKeyword(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Notice findById(Long id) {
        return noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
    }

    @Transactional
    public Notice update(Long id, NoticeUpdateRequest request) {
        Notice notice = findById(id);
        notice.update(request.getTitle(), request.getContent(),
                request.getStartsAt(), request.getEndsAt(), request.isServiceMaintenance(),
                request.getScheduledAt(), request.getMaintenanceStartAt(), request.getMaintenanceEndAt());
        return notice;
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = findById(id);
        notice.delete();
    }

    @Transactional
    public void changeStatus(Long id, NoticeStatus status) {
        Notice notice = findById(id);
        if (status == NoticeStatus.SCHEDULED && notice.getScheduledAt() == null) {
            throw new ValidationFailedException("예약 발행 시간이 설정되지 않은 공지사항은 SCHEDULED 상태로 변경할 수 없습니다.");
        }
        notice.changeStatus(status);
    }
}