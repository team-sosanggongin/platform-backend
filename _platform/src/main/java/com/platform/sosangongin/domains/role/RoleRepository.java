package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByCreatedBy(User user);
    List<Role> findByCreatedByAndPlatformType(User user, PlatformType platformType);
}
