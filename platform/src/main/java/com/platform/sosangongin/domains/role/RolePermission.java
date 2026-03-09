package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "role_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long permissionId;

    @Column(name = "permission_name")
    private String permissionName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "perm_domain", nullable = false))
    private PermissionDomain permDomain;

}
