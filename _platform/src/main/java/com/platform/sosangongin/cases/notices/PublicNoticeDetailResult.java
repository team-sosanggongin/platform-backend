package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class PublicNoticeDetailResult extends CommonResultTemplate {

    private final PublicNoticeVo publicNotice;

    @Builder
    public PublicNoticeDetailResult(HttpStatus httpStatus, String message, PublicNoticeVo vo) {
        super(httpStatus, message);
        this.publicNotice = vo;
    }
}
