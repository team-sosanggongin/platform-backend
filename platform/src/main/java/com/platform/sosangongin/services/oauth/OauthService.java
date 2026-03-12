package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;

public interface OauthService {
    String buildAuthorizeUrl(SocialProvider provider) throws IllegalArgumentException;
    AuthResponse getAuth(SocialProvider provider, String code);
}
