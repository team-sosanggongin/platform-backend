package com.backoffice.sosangongin.role.repository;

import com.backoffice.sosangongin.role.domain.AccountRole;
import com.backoffice.sosangongin.role.domain.AccountRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRoleId> {
    List<AccountRole> findByIdAccountId(UUID accountId);
    void deleteByIdAccountId(UUID accountId);
}