package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "businesses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "biz_name", nullable = false)
    private String bizName;

    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", nullable = false)
    private BusinessType bizType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BusinessStatus status;

    /**
     * @throws IllegalStateException 해당 엔티티의 Owner 정보가 없을 경우
     */
    public boolean isRealOwner(UUID inviterId) throws IllegalStateException{
        if(this.owner == null){
            this.status = BusinessStatus.ILLEGAL_STATE;
            throw new IllegalStateException("this business "+this.id+" do not have owner and this violate date integrity");
        }
        return this.owner.getId().equals(inviterId);
    }
}
