package com.platform.sosangongin.services.oauth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
@Getter
@Setter
public class KakaoProperties {

    private String clientId;

}
