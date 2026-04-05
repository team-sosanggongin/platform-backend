package com.backoffice.sosangongin.role.domain;

import com.backoffice.sosangongin.global.entity.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public void updatePermissions(List<RolePermission> newPermissions) {
        this.rolePermissions.clear();
        this.rolePermissions.addAll(newPermissions);
    }
}