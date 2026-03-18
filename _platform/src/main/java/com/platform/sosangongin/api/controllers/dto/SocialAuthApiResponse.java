package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.auth.social.SocialAuthResult;
import lombok.Getter;

@Getter
public class SocialAuthApiResponse extends CommonResultTemplate {
    private final String url;
    private final String provider;

    public SocialAuthApiResponse(SocialAuthResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.url = result.getUrl();
        this.provider = result.getProvider();
    }
}
