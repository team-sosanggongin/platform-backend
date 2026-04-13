package com.backoffice.sosangongin.role.domain;

import com.backoffice.sosangongin.global.entity.SoftDeletedBaseEntity;
import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role_backoffice")
public class RoleBackoffice extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private UUID createdBy;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RolePermission> rolePermissions = new ArrayList<>();

    public void update(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public void replacePermissions(List<PermissionBackoffice> targets) {
        Set<Long> targetIds = targets.stream()
                .map(PermissionBackoffice::getId)
                .collect(Collectors.toSet());

        rolePermissions.removeIf(rp -> !targetIds.contains(rp.getPermission().getId()));

        Set<Long> existingIds = rolePermissions.stream()
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toCollection(HashSet::new));

        for (PermissionBackoffice permission : targets) {
            if (existingIds.contains(permission.getId())) {
                continue;
            }
            rolePermissions.add(RolePermission.builder()
                    .id(new RolePermissionId(this.id, permission.getId()))
                    .role(this)
                    .permission(permission)
                    .build());
        }
    }
}
