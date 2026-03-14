package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class RefreshTokenRequest extends CommonRequestTemplate {
    private final String refreshToken;
}
