package com.platform.sosangongin.cases.invitation;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.business.BusinessRepository;
import com.platform.sosangongin.domains.invitation.Invitation;
import com.platform.sosangongin.domains.invitation.InvitationRepository;
import com.platform.sosangongin.domains.invitation.InvitationStatus;
import com.platform.sosangongin.domains.role.BusinessRole;
import com.platform.sosangongin.domains.role.BusinessRoleRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.external.SmsPushService;
import com.platform.sosangongin.services.messages.MessageTemplate;
import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class UserInvitationUsecase {

    /**
     * 초대 대상이 앱이 깔려 있고, 회원 가입이 완료된 경우
     * TODO :: 초대 대상이 앱은 깔았으나, 회원 가입이 완료되지 않은 경우
     * TODO :: 초대 대상이 앱은 깔지 않았으나, 회원 가입이 완료된 경우
     * TODO :: 초대 대상이 앱도, 회원가입도 하지 않은 경우
     */
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final RandomCharGeneratorService randomCharGeneratorService;
    private final InvitationRepository invitationRepository;
    private final BusinessRoleRepository businessRoleRepository;
    private final SmsPushService smsPushService;
    private final MessageTemplate messageTemplate;
    /**
     * 유저 초대 로직
     * @apiNote 초대 대상이 앱이 깔려 있고, 회원 가입이 완료된 경우
     * @param request 초대 요청 정보 (번호, 지점ID, 역할ID 리스트 등)
     */
    @Transactional
    public InviteResult inviteAlreadySingedUpUser(InviteRequest request) {

        UUID inviterId = request.getInviterId();
        UUID branchId = request.getBranchId();
        String targetUserPhoneNumber = request.getTargetUserPhoneNumber();

        log.debug("user {} is trying to invite new User to {}", inviterId, branchId);

        Optional<Business> optionalBusiness = this.businessRepository.findById(branchId);

        if (optionalBusiness.isEmpty()) {
            log.warn("this business {} is not present", branchId);
            return InviteResult.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("business is not present")
                    .build();
        }

        Business business = optionalBusiness.get();

        try {
            if (!business.isRealOwner(inviterId)) {
                return InviteResult.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("invitation only can be made by the owner")
                        .build();
            }
        } catch (IllegalStateException e) {
            log.error("", e);
            return InviteResult.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("server error")
                    .build();
        }

        Optional<User> targetUserOptional = this.userRepository.findByPhoneNumber(targetUserPhoneNumber);

        if (targetUserOptional.isEmpty()) {
            log.debug("target user {} is not present", targetUserPhoneNumber);
            return InviteResult.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("target user is not present. check the phone number")
                    .build();
        }

        String invitationCode = this.randomCharGeneratorService.getRandomChar(6, inviterId + "_" + branchId + "_" + LocalDateTime.now());
        User invitee = targetUserOptional.get();

        List<Long> roleIds = request.getRoleIds();

        User owner = business.getOwner();
        Invitation invitation = Invitation.builder()
                .inviter(owner)
                .targetPhoneNumber(targetUserPhoneNumber)
                .invitee(invitee)
                .invitationCode(invitationCode)
                .status(InvitationStatus.PENDING)
                .business(business)
                .expiresAt(LocalDateTime.now().plusDays(3))
                .build();

        List<BusinessRole> businessRoles = this.businessRoleRepository.findAllById(roleIds);

        invitation.addRoles(businessRoles);

        this.invitationRepository.save(invitation);

        //TODO :: kakao, sms 등으로 처리 필요

        this.smsPushService.send(targetUserPhoneNumber, this.messageTemplate.getInvitationTemplate(owner.getName(), invitee.getName(), invitationCode));

        return InviteResult.builder()
                .httpStatus(HttpStatus.OK)
                .build();

    }
}
