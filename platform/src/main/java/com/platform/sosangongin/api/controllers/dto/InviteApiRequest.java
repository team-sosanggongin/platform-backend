package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.invitation.InviteRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InviteApiRequest {
    private UUID inviterId;
    private UUID branchId;
    private String targetUserPhoneNumber;
    private List<Long> roleIds;

    public InviteRequest toUseCaseRequest() {
        return new InviteRequest(inviterId, branchId, targetUserPhoneNumber, roleIds);
    }
}
