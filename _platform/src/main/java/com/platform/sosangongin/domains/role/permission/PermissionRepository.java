package com.platform.sosangongin.domains.role.permission;

import com.platform.sosangongin.domains.role.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByPlatformTypeAndIsActiveTrueAndDeletedAtIsNull(PlatformType platformType);
}
