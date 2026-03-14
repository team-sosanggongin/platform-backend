package com.platform.sosangongin.cases.registration;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class JoinBusinessRequest extends CommonRequestTemplate {
    private final UUID userId;
    private final UUID businessId;

    @Builder
    public JoinBusinessRequest(UUID userId, UUID businessId) {
        this.userId = userId;
        this.businessId = businessId;
    }
}
