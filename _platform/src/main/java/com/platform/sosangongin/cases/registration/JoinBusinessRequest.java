package com.platform.sosangongin.cases.registration;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class JoinBusinessRequest extends CommonRequestTemplate {
    private final UUID userId;
    private final UUID businessId;
}
