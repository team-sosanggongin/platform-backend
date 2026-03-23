package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.notices.PublicNoticeDetailResult;
import com.platform.sosangongin.cases.notices.PublicNoticeVo;
import lombok.Getter;

@Getter
public class PublicNoticeDetailApiResponse extends CommonResultTemplate {
    private final PublicNoticeVo publicNotice;

    public PublicNoticeDetailApiResponse(PublicNoticeDetailResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.publicNotice = result.getPublicNotice();
    }
}
