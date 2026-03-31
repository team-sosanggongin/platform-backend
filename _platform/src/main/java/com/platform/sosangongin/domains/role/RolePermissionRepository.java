package com.platform.sosangongin.domains.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);
    void deleteAllByRole(Role role);
}
