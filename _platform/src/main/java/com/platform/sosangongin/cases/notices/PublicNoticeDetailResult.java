package com.platform.sosangongin.cases.notices;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PublicNoticeDetailResult {

    private final PublicNoticeVo publicNotice;

    @Builder
    public PublicNoticeDetailResult(PublicNoticeVo vo) {
        this.publicNotice = vo;
    }
}
