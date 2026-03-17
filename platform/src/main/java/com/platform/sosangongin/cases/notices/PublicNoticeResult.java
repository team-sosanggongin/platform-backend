package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;


@Getter
@ToString
public class PublicNoticeResult extends CommonResultTemplate {

   private final Page<PublicNoticeVo> publicNotices;

    public PublicNoticeResult(HttpStatus httpStatus, String message, Page<PublicNoticeVo> notices) {
        super(httpStatus, message);
        this.publicNotices = notices;
    }

}
