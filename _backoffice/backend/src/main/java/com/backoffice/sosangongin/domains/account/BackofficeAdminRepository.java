package com.backoffice.sosangongin.domains.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BackofficeAdminRepository extends JpaRepository<BackofficeAdmin, UUID> {
    Optional<BackofficeAdmin> findByLoginId(String loginId);
}