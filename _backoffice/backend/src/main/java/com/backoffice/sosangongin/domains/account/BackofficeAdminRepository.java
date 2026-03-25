package com.backoffice.sosangongin.domains.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BackofficeAdminRepository extends JpaRepository<BackofficeAdmin, UUID> {
    Optional<BackofficeAdmin> findByLoginId(String loginId);
}