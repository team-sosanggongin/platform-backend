package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.business.location.BusinessMetadata;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BusinessDto {
    private UUID businessId;
    private String bizName;
    private String bizType;
    private String status;


    public static BusinessDto from(Business business) {
        BusinessDtoBuilder builder = BusinessDto.builder()
                .businessId(business.getId())
                .bizName(business.getBizName())
                .bizType(business.getBizType().name())
                .status(business.getStatus().name());
        return builder.build();
    }
}
