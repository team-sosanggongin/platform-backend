package com.platform.sosangongin.cases.auth.social;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class SocialAuthResult extends CommonResultTemplate {
    private final String url;
    private final String provider;
    @Builder
    public SocialAuthResult(HttpStatus httpStatus, String message, String url, String provider) {
        super(httpStatus, message);
        this.url = url;
        this.provider = provider;
    }

}
