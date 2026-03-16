package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class PublicNoticeRequest extends CommonRequestTemplate {
    private final int page;
    private final int size;
}
