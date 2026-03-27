package com.platform.sosangongin.services.codegen;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@EqualsAndHashCode
public class CodeVo {
    private final String code;
    private final String version;

    protected CodeVo(String code, String version) {
        this.code = code;
        this.version = version;
    }
}
