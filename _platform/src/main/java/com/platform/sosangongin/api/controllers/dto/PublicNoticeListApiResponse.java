package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.notices.PublicNoticeResult;
import com.platform.sosangongin.cases.notices.PublicNoticeVo;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PublicNoticeListApiResponse extends CommonResultTemplate {
    private final Page<PublicNoticeVo> publicNotices;

    public PublicNoticeListApiResponse(PublicNoticeResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.publicNotices = result.getPublicNotices();
    }
}
