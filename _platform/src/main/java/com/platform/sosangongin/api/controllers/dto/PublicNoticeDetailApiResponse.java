package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.notices.PublicNoticeDetailResult;
import com.platform.sosangongin.cases.notices.PublicNoticeVo;
import lombok.Getter;

@Getter
public class PublicNoticeDetailApiResponse {
    private final PublicNoticeVo publicNotice;

    public PublicNoticeDetailApiResponse(PublicNoticeDetailResult result) {
        this.publicNotice = result.getPublicNotice();
    }
}
