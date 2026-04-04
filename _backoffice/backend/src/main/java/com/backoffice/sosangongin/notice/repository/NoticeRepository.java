package com.backoffice.sosangongin.notice.repository;

import com.backoffice.sosangongin.notice.domain.Notice;
import com.backoffice.sosangongin.notice.domain.NoticeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByDeletedAtIsNull();
    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);
    List<Notice> findByStatusAndDeletedAtIsNull(NoticeStatus status);
}