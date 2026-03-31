package com.platform.sosangongin.domains.bankaccount;

import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByUserAndDeletedAtIsNull(User user);
    Optional<BankAccount> findByIdAndUserAndDeletedAtIsNull(UUID id, User user);
}
