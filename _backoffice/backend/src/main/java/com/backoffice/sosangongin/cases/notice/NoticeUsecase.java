package com.backoffice.sosangongin.cases.notice;

import com.backoffice.sosangongin.domains.notice.BackofficeNotice;
import com.backoffice.sosangongin.domains.notice.BackofficeNoticeRepository;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeUsecase {

    private final BackofficeNoticeRepository noticeRepository;

    public BackofficeNotice createNotice(CreateNoticeCommand command) {
        BackofficeNotice notice = BackofficeNotice.create(command.toNoticeContent());
        return noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public BackofficeNotice getNotice(Long noticeId) {
        return noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));
    }

    @Transactional(readOnly = true)
    public List<BackofficeNotice> getAllNotices() {
        return noticeRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    }

    public BackofficeNotice updateNotice(Long noticeId, UpdateNoticeCommand command) {
        BackofficeNotice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));

        notice.update(command.toNoticeContent());
        return notice;
    }

    public void deleteNotice(Long noticeId) {
        BackofficeNotice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));

        notice.delete();
    }
}
