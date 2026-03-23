package com.platform.sosangongin.cases.registration;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JoinBusinessResult extends CommonResultTemplate {

    @Builder
    public JoinBusinessResult(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
