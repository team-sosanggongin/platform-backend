package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("test")
public class TestOauthServiceImpl implements OauthService{
    @Override
    public String buildAuthorizeUrl(SocialProvider provider) {
        return "";
    }

    @Override
    public AuthResponse getAuth(SocialProvider provider, String code) {
        return new AuthResponse(provider, UUID.randomUUID().toString(), "testName","01000000000");
    }
}
