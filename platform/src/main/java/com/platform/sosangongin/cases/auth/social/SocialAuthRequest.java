package com.platform.sosangongin.cases.auth.social;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import com.platform.sosangongin.domains.user.SocialProvider;
import lombok.Getter;

@Getter
public class SocialAuthRequest extends CommonRequestTemplate {
    private final SocialProvider provider;

    public SocialAuthRequest(String provider) {
        this.provider = SocialProvider.valueOf(provider.toUpperCase());
    }
}
