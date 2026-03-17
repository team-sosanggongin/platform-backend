package com.platform.sosangongin.domains.notices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PublicNoticeRepository extends JpaRepository<PublicNotice, Long> {

    /**
     * 유저용 공지사항 조회 쿼리
     * 1. PUBLISHED 상태일 것
     * 2. 시작일(startsAt)이 현재 시간보다 이전일 것
     * 3. 종료일(endsAt)이 없거나, 현재 시간보다 이후일 것
     * 4. 상단 고정(isPinned) 우선, 그 다음 최신순(startsAt desc) 정렬
     */
    @Query("SELECT n FROM PublicNotice n " +
            "WHERE n.status = 'PUBLISHED' " +
            "AND n.startsAt <= :now " +
            "AND (n.endsAt IS NULL OR n.endsAt >= :now) " +
            "AND n.deletedAt IS NULL " +
            "ORDER BY n.isPinned DESC, n.startsAt DESC")
    Page<PublicNotice> findActiveNotices(@Param("now") LocalDateTime now, Pageable pageable);

}
