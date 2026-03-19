package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.role.Role;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "employment_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmploymentRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_id", nullable = false)
    private Employment employment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
