package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import com.platform.sosangongin.domains.user.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class LoginRequest extends CommonRequestTemplate {
    private final String code;
    private final SocialProvider provider;

    public void parseState(String code, String state){
        //state에 들어간 값을 기반으로, LoginResult 코드 생성
    }
}
