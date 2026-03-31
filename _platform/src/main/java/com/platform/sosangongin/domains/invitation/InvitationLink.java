package com.platform.sosangongin.domains.invitation;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.role.Role;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class InvitationLink extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 초대한 사람 (사장님 혹은 관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    // 초대받은 사람 (InvitationType.SPECIFIC_USER_INVITATION일 경우에만 존재)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    // 수락 시 부여될 다중 역할들 (Cascade 설정으로 함께 저장)
    @Builder.Default
    @OneToMany(mappedBy = "invitation_link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvitationRole> invitationRoles = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private InvitationType invitationType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public void addRoles(List<Role> roles){
        List<InvitationRole> invitationRoles = roles.stream().map(next -> InvitationRole.builder()
                        .role(next)
                        .invitationLink(this)
                        .build())
                .toList();
        this.invitationRoles.addAll(invitationRoles);
    }
}