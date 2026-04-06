package com.backoffice.sosangongin.role.domain;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_roles_backoffice")
public class AccountRole {

    @EmbeddedId
    private AccountRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private BackofficeAdmin account;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleBackoffice role;
}