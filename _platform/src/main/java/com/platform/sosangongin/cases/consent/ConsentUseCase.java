package com.platform.sosangongin.cases.consent;

import com.platform.sosangongin.domains.consent.*;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.EntityType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class ConsentUseCase {

    private final UserRepository userRepository;
    private final ConsentPolicyRepository consentPolicyRepository;
    private final UserConsentRepository userConsentRepository;

    /**
     * 사용자가 아직 동의하지 않은 활성 약관 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public PendingConsentResult getPendingConsents(String userId) {
        User user = findUser(userId);

        List<ConsentPolicy> activePolicies = consentPolicyRepository.findByIsActiveTrue();

        Set<Long> agreedPolicyIds = userConsentRepository.findByUser(user).stream()
                .filter(UserConsent::isAgreed)
                .map(uc -> uc.getConsentPolicy().getId())
                .collect(Collectors.toSet());

        List<PendingConsentVo> pendingConsents = activePolicies.stream()
                .filter(policy -> !agreedPolicyIds.contains(policy.getId()))
                .map(PendingConsentVo::from)
                .toList();

        return PendingConsentResult.builder()
                .pendingConsents(pendingConsents)
                .allAgreed(pendingConsents.isEmpty())
                .build();
    }

    /**
     * 사용자가 특정 약관에 동의/철회한다.
     */
    @Transactional
    public AgreeConsentResult agreeConsent(AgreeConsentRequest request) {
        User user = findUser(request.getUserId());

        ConsentPolicy policy = consentPolicyRepository.findById(request.getConsentPolicyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        request.getConsentPolicyId(), EntityType.CONSENT_POLICY, "consent policy not found"));

        UserConsent userConsent = userConsentRepository.findByUserAndConsentPolicy(user, policy)
                .orElseGet(() -> UserConsent.builder()
                        .user(user)
                        .consentPolicy(policy)
                        .isAgreed(false)
                        .build());

        if (request.isAgreed()) {
            userConsent.agree();
        } else {
            userConsent.revoke();
        }

        userConsentRepository.save(userConsent);

        return AgreeConsentResult.builder()
                .success(true)
                .build();
    }

    private User findUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(
                        userId, EntityType.USER, "user not found"));
    }
}
