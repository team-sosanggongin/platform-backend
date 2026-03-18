package com.platform.sosangongin.cases.registration;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.business.BusinessJoinRequest;
import com.platform.sosangongin.domains.business.BusinessJoinRequestRepository;
import com.platform.sosangongin.domains.business.BusinessJoinRequestStatus;
import com.platform.sosangongin.domains.business.BusinessRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.external.SmsPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JoinBusinessUsecase {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessJoinRequestRepository businessJoinRequestRepository;
    private final SmsPushService smsPushService;

    @Transactional
    public JoinBusinessResult join(JoinBusinessRequest request) {
        log.info("User {} requested to join business {}", request.getUserId(), request.getBusinessId());

        // 1. 유저 검증
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", request.getUserId());
            return JoinBusinessResult.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();

        // 2. 사업체 검증
        Optional<Business> businessOptional = businessRepository.findById(request.getBusinessId());
        if (businessOptional.isEmpty()) {
            log.warn("Business not found: {}", request.getBusinessId());
            return JoinBusinessResult.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Business not found")
                    .build();
        }
        Business business = businessOptional.get();

        // 3. 중복 요청 검증 (이미 대기 중이거나 승인된 상태인지 확인)
        boolean alreadyRequested = businessJoinRequestRepository.existsByUserAndBusinessAndStatusIn(
                user, business, Arrays.asList(BusinessJoinRequestStatus.PENDING, BusinessJoinRequestStatus.APPROVED)
        );
        if (alreadyRequested) {
            log.warn("Join request already exists or approved for user {} and business {}", user.getId(), business.getId());
            return JoinBusinessResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Already requested or approved")
                    .build();
        }

        // 4. 요청 저장
        BusinessJoinRequest joinRequest = BusinessJoinRequest.builder()
                .user(user)
                .business(business)
                .status(BusinessJoinRequestStatus.PENDING)
                .build();
        businessJoinRequestRepository.save(joinRequest);

        User owner = business.getOwner();
        if (owner != null && owner.getPhoneNumber() != null) {
            String message = String.format("[소상공인플랫폼] %s님이 %s 사업체에 직원 등록을 요청했습니다.", user.getName(), business.getBizName());
            smsPushService.send(owner.getPhoneNumber(), message);
            log.info("Sent SMS to owner {} regarding join request from user {}", owner.getId(), user.getId());
        }

        return JoinBusinessResult.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully requested to join business")
                .build();
    }
}
