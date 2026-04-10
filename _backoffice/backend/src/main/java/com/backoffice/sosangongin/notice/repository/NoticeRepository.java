package com.backoffice.sosangongin.notice.repository;

import com.backoffice.sosangongin.notice.domain.Notice;
import com.backoffice.sosangongin.notice.domain.NoticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByDeletedAtIsNull();
    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);
    List<Notice> findByStatusAndDeletedAtIsNull(NoticeStatus status);

    Page<Notice> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE n.deletedAt IS NULL " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Notice> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}