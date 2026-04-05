package com.backoffice.sosangongin.permission.domain;

import com.backoffice.sosangongin.global.entity.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permission_backoffice")
public class PermissionBackoffice extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_name", nullable = false)
    private String permissionName;

    @Column(name = "perm_domain", nullable = false)
    private String permDomain;

    public void update(String permissionName, String permDomain) {
        this.permissionName = permissionName;
        this.permDomain = permDomain;
    }
}