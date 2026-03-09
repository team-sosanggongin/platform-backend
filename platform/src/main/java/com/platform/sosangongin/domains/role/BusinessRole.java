package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    public BusinessRole(Business business, String roleName) {
        this.business = business;
        this.roleName = roleName;
    }
}
