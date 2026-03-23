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
    // 이전에는 클라이언트가 userId를 보냈지만, 이제는 JWT 토큰에서 추출하므로 Body에는 데이터가 없거나 다른 부가 정보만 들어옵니다.
    // 현재는 특별한 추가 데이터가 필요 없으므로 비워둡니다.

    public JoinBusinessRequest toUseCaseRequest(UUID userId, UUID businessId) {
        return new JoinBusinessRequest(userId, businessId);
    }
}
