package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.registration.JoinBusinessRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class JoinBusinessApiRequest {
    private UUID userId;

    public JoinBusinessRequest toUseCaseRequest(UUID businessId) {
        return new JoinBusinessRequest(this.userId, businessId);
    }
}
