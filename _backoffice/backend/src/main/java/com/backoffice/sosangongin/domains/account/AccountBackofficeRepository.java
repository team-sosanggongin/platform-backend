package com.backoffice.sosangongin.domains.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountBackofficeRepository extends JpaRepository<AccountBackoffice, UUID> {
    Optional<AccountBackoffice> findByLoginId(String loginId);
}
