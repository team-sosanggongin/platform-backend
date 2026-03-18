package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.controllers.dto.InviteApiRequest;
import com.platform.sosangongin.api.controllers.dto.InviteApiResponse;
import com.platform.sosangongin.api.resolver.LoginUser;
import com.platform.sosangongin.cases.invitation.UserInvitationUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Invitation", description = "초대 관련 API")
@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final UserInvitationUsecase userInvitationUsecase;

    @Operation(summary = "사용자 초대", description = "사장님이 전화번호를 통해 직원을 사업체에 초대합니다.")
    @PostMapping
    public InviteApiResponse inviteUser(
            @LoginUser UUID inviterId,
            @RequestBody InviteApiRequest request) {
        
        return new InviteApiResponse(userInvitationUsecase.inviteAlreadySingedUpUser(request.toUseCaseRequest(inviterId)));
    }
}
