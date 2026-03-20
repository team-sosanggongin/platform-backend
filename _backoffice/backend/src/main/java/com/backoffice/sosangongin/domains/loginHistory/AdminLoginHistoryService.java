package com.backoffice.sosangongin.domains.loginHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminLoginHistoryService {

    private final AdminLoginHistoryRepository adminLoginHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLoginAttempt(UUID accountId, String ipAddress, String userAgent, boolean isSuccess) {
        AdminLoginHistory history = AdminLoginHistory.builder()
                .accountId(accountId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isSuccess(isSuccess)
                .build();
        adminLoginHistoryRepository.save(history);
    }
}
