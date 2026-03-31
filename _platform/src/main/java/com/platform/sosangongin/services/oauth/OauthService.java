package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;

public interface OauthService {
    AuthResponse getAuth(SocialProvider provider, String code);
}
