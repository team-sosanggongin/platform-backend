package com.platform.sosangongin.cases.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class JoinBusinessRequest {
    private final UUID userId;
    private final UUID businessId;
}
