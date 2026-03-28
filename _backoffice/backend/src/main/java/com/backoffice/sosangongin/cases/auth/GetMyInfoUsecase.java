package com.backoffice.sosangongin.cases.auth;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.domains.account.BackofficeAdminRepository;
import com.backoffice.sosangongin.errors.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyInfoUsecase {

    private final BackofficeAdminRepository backofficeAdminRepository;

    @Transactional(readOnly = true)
    public BackofficeAdmin getMyInfo(UUID accountId) {
        return backofficeAdminRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("계정을 찾을 수 없습니다. id=" + accountId));
    }
}