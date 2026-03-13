package com.platform.sosangongin.cases.invitation;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class InviteRequest extends CommonRequestTemplate {
    private final UUID inviterId;
    private final UUID branchId;
    private final String targetUserPhoneNumber;
    private final List<Long> roleIds;
}
