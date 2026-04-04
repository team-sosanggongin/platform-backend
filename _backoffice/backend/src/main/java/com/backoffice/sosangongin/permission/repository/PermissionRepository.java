package com.backoffice.sosangongin.permission.repository;

import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<PermissionBackoffice, Long> {
    List<PermissionBackoffice> findByDeletedAtIsNull();
    List<PermissionBackoffice> findByIdInAndDeletedAtIsNull(List<Long> ids);
    boolean existsByPermissionNameAndPermDomain(String permissionName, String permDomain);
}