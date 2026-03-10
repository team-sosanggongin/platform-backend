package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;

public interface OauthService {
    String buildAuthorizeUrl(String provider);
    AuthResponse getAuth(SocialProvider provider, String code);
}
