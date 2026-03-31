package com.backoffice.sosangongin.domains.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BackofficeNoticeRepository extends JpaRepository<BackofficeNotice, Long> {

    Optional<BackofficeNotice> findByIdAndDeletedAtIsNull(Long id);

    List<BackofficeNotice> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
}
