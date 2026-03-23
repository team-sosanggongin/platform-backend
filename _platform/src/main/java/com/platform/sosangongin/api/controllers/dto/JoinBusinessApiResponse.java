package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.registration.JoinBusinessResult;
import lombok.Getter;

@Getter
public class JoinBusinessApiResponse extends CommonResultTemplate {
    public JoinBusinessApiResponse(JoinBusinessResult result) {
        super(result.getHttpStatus(), result.getMessage());
    }
}
