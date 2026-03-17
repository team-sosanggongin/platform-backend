package com.platform.sosangongin.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class CommonResultTemplate {
    private final HttpStatus httpStatus;
    private final String message;
    private final String nextUrl;

    public CommonResultTemplate(HttpStatus httpStatus, String message, String next) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.nextUrl = next;
    }

    public CommonResultTemplate(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.nextUrl = "";
    }
}
