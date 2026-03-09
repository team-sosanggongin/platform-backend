package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.common.BaseLongIdEntity;
import com.platform.sosangongin.domains.role.BusinessRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employment_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmploymentRole extends BaseLongIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_id", nullable = false)
    private Employment employment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private BusinessRole role;

    public EmploymentRole(Employment employment, BusinessRole role) {
        this.employment = employment;
        this.role = role;
    }
}
