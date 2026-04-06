package com.backoffice.sosangongin.role.repository;

import com.backoffice.sosangongin.role.domain.RoleBackoffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleBackoffice, Long> {
    boolean existsByRoleName(String roleName);
    Optional<RoleBackoffice> findByIdAndDeletedAtIsNull(Long id);
    List<RoleBackoffice> findByDeletedAtIsNull();
}