package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "role_description")
    private String description;

    @Column(name = "is_recommended", columnDefinition = "데이터에 의거, 관리자에 의해 생성 및 추천되는 롤")
    private boolean isRecommended;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private Set<RolePermission> rolePermissionSet = new HashSet<>();

}
