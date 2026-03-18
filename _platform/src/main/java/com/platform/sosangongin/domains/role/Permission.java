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
public class Permission extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_name")
    private String permissionName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "perm_domain", nullable = false))
    private PermissionDomain permDomain;

}
