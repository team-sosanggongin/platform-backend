package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.social.SocialAuthRequest;
import com.platform.sosangongin.domains.user.SocialProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SocialAuthApiRequest {
    private SocialProvider provider;

    public SocialAuthRequest toUseCaseRequest() {
        return new SocialAuthRequest(this.provider);
    }
}
