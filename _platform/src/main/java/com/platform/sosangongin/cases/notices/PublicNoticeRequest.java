package com.platform.sosangongin.cases.notices;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class PublicNoticeRequest {
    private final int page;
    private final int size;
}
