package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.invitation.InviteResult;
import lombok.Getter;

@Getter
public class InviteApiResponse extends CommonResultTemplate {
    public InviteApiResponse(InviteResult result) {
        super(result.getHttpStatus(), result.getMessage());
    }
}
