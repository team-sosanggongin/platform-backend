package com.platform.sosangongin.cases.auth.social;

import com.platform.sosangongin.services.oauth.OauthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SocialAuthRedirectCase {

    private final OauthService oauthService;

    public SocialAuthResult getRedirectionUrl(SocialAuthRequest req){

        try{
            String url = this.oauthService.buildAuthorizeUrl(req.getProvider());
            return SocialAuthResult.builder()
                    .httpStatus(HttpStatus.OK)
                    .url(url)
                    .provider(req.getProvider().name())
                    .build();
        }catch (IllegalArgumentException e){
            log.warn("unsupported provider {} is referenced", req.getProvider());
            return SocialAuthResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

}
