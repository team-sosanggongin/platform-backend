package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;

public record AuthResponse(SocialProvider provider, String uniqueId, String userName, String phoneNumber) {
    public String uniqueIdWithProvider(){
        return this.provider.name()+"-"+this.uniqueId;
    }
}
