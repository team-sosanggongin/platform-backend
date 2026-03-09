package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.common.BaseLongIdEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermission extends BaseLongIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private BusinessRole role;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "perm_domain", nullable = false))
    private PermissionDomain permDomain;

    public RolePermission(BusinessRole role, PermissionDomain permDomain) {
        this.role = role;
        this.permDomain = permDomain;
    }
}
