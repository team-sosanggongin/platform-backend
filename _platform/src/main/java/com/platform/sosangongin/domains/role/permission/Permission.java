package com.platform.sosangongin.domains.role.permission;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.role.PlatformType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "permissions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_permission_name", columnNames = {"permission_name", "platform_type", "is_deleted"}),
        @UniqueConstraint(name = "uk_permission_domain", columnNames = {"perm_domain", "platform_type", "is_deleted"})
})
public class Permission extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_name", unique = true)
    private String permissionName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "perm_domain", nullable = false, unique = true))
    private PermissionDomain permDomain;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType;

    @Column(name = "is_active")
    private boolean isActive;

}
