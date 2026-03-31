package com.platform.sosangongin.services.oauth;

import com.platform.sosangongin.domains.user.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@Profile({"localApi","stg","prod"})
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final WebClient webClient;

    @Override
    public AuthResponse getAuth(SocialProvider provider, String code) {
        ClientRegistration registration = getClientRegistration(provider);

        Map<String, Object> tokenResponse = requestToken(registration, code);
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = requestUserInfo(registration, accessToken);

        return OauthDataFactory.parse(provider, userInfo);
    }

    private ClientRegistration getClientRegistration(SocialProvider provider) {
        String registrationId = provider.name().toLowerCase();
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
        return registration;
    }

    private Map<String, Object> requestToken(ClientRegistration registration, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", registration.getClientId());
        params.add("client_secret", registration.getClientSecret());
        params.add("redirect_uri", registration.getRedirectUri());
        params.add("code", code);

        try {
            return webClient.post()
                    .uri(registration.getProviderDetails().getTokenUri())
                    .body(BodyInserters.fromFormData(params))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .block();
        } catch (Exception e) {
            log.error("토큰 요청 실패: provider={}, error={}", registration.getRegistrationId(), e.getMessage());
            throw new OAuth2AuthorizationException(null, "토큰 요청에 실패했습니다.", e);
        }
    }

    private Map<String, Object> requestUserInfo(ClientRegistration registration, String accessToken) {
        try {
            return webClient.get()
                    .uri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
                    .headers(h -> h.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .cast(Map.class)
                    .map(m -> (Map<String, Object>) m)
                    .block();
        } catch (Exception e) {
            log.error("사용자 정보 요청 실패: provider={}, error={}", registration.getRegistrationId(), e.getMessage());
            throw new OAuth2AuthorizationException(null, "사용자 정보 요청에 실패했습니다.", e);
        }
    }
}
