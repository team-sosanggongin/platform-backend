package com.platform.sosangongin.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@ToString
public class CommonResultTemplate {
    private final HttpStatus httpStatus;
    private final String message;
}
