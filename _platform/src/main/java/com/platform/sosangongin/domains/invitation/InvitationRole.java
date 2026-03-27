package com.platform.sosangongin.domains.invitation;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.role.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invitation_roles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_invitation_role", columnNames = {"invitation_id", "role_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InvitationRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id", nullable = false)
    private InvitationLink invitationLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}