package com.backoffice.sosangongin.auth.repos;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<BackofficeAdmin, UUID> {
    Optional<BackofficeAdmin> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}