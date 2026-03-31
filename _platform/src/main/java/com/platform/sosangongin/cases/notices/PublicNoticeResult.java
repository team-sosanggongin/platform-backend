package com.platform.sosangongin.cases.notices;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;


@Getter
@ToString
public class PublicNoticeResult {

   private final Page<PublicNoticeVo> publicNotices;

    public PublicNoticeResult(Page<PublicNoticeVo> notices) {
        this.publicNotices = notices;
    }

}
