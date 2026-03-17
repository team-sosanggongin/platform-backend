package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class OauthServiceImpl implements OauthService{

    @Override
    public String buildAuthorizeUrl(SocialProvider provider) {
        return "";
    }

    @Override
    public AuthResponse getAuth(SocialProvider provider, String code) {
        return null;
    }
}
