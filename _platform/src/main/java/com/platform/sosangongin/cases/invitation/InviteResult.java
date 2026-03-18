package com.platform.sosangongin.cases.invitation;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter

public class InviteResult extends CommonResultTemplate {

    @Builder
    public InviteResult(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
