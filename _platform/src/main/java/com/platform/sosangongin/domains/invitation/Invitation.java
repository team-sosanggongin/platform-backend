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

@Entity
@Table(name = "invitations")
@Getter
@AllArgsConstructor
@Builder
@Setter
public class Invitation extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 초대한 사람 (사장님 혹은 관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    // 초대받은 사람 (가입 완료 및 수락 시점에 세팅)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    // 초대받을 사람의 전화번호 (미가입자 식별용)
    @Column(name = "target_phone_number", nullable = false)
    private String targetPhoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    // 수락 시 부여될 다중 역할들 (Cascade 설정으로 함께 저장)
    @OneToMany(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvitationRole> invitationRoles = new ArrayList<>();

    // 디퍼드 딥링크에서 사용할 고유 식별 코드 (예: 랜덤 8자리 또는 UUID)
    @Column(name = "invitation_code", unique = true, nullable = false)
    private String invitationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 초대장 만료 여부 확인
     */
    public boolean isExpired(LocalDateTime now) {
        return this.status == InvitationStatus.EXPIRED ||
                this.expiresAt.isBefore(now);
    }

    /**
     * 초대 수락 처리
     * @param user 가입/로그인 완료된 수락자 객체
     * @throws IllegalArgumentException user 객체와 invitation 객체가 매칭되지 않는 경우(전화번호 등이 다른 경우)
     * @throws IllegalStateException 이미 해당 초대장의 유효기간이 종료된 경우
     */
    public void accept(LocalDateTime now, User user) throws IllegalStateException, IllegalArgumentException{
        if (isExpired(now)) {
            throw new IllegalStateException("만료된 초대장입니다.");
        }
        if (!this.targetPhoneNumber.equals(user.getPhoneNumber())) {
            throw new IllegalArgumentException("초대받은 전화번호와 일치하지 않는 사용자입니다.");
        }
        this.invitee = user;
        this.status = InvitationStatus.ACCEPTED;
    }

    /**
     * 다중 역할 추가 메서드
     */
    public void addRole(Role role) {
        InvitationRole invitationRole = InvitationRole.builder()
                .invitation(this)
                .role(role)
                .build();
        this.invitationRoles.add(invitationRole);
    }

    public void addRoles(List<Role> roles){
        List<InvitationRole> invitationRoles = roles.stream().map(next -> InvitationRole.builder()
                        .role(next)
                        .invitation(this)
                        .build())
                .toList();
        this.invitationRoles.addAll(invitationRoles);
    }
}