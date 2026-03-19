package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "permissions")
public class Permission extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_name")
    private String permissionName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "perm_domain", nullable = false))
    private PermissionDomain permDomain;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type",nullable = false, columnDefinition = "권한이 사용되는 서비스 - BACKOFFICE, PLATFORM")
    private PermissionType permissionType;

    @Column(name = "is_active")
    private boolean isActive;

}
