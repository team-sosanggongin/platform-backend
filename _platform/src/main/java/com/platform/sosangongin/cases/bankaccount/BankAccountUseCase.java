package com.platform.sosangongin.cases.bankaccount;

import com.platform.sosangongin.domains.bankaccount.BankAccount;
import com.platform.sosangongin.domains.bankaccount.BankAccountRepository;
import com.platform.sosangongin.domains.bankaccount.BankAccountVo;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.EntityType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class BankAccountUseCase {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional(readOnly = true)
    public BankAccountListResult getAccounts(String userId) throws EntityNotFoundException {
        User user = findUser(userId);

        List<BankAccountVo> accounts = bankAccountRepository.findByUserAndDeletedAtIsNull(user).stream()
                .map(BankAccountVo::from)
                .toList();

        return BankAccountListResult.builder()
                .accounts(accounts)
                .build();
    }

    @Transactional
    public BankAccountResult createAccount(String userId, BankAccountRequest request) throws EntityNotFoundException {
        User user = findUser(userId);

        BankAccount bankAccount = BankAccount.builder()
                .user(user)
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountAlias(request.getAccountAlias())
                .build();

        bankAccountRepository.save(bankAccount);

        return BankAccountResult.builder()
                .success(true)
                .account(BankAccountVo.from(bankAccount))
                .build();
    }

    @Transactional
    public BankAccountResult updateAccount(String userId, String bankAccountId, BankAccountRequest request) throws EntityNotFoundException {
        User user = findUser(userId);
        BankAccount bankAccount = findBankAccount(bankAccountId, user);

        bankAccount.update(request.getBankName(), request.getAccountNumber(), request.getAccountAlias());

        return BankAccountResult.builder()
                .success(true)
                .account(BankAccountVo.from(bankAccount))
                .build();
    }

    @Transactional
    public BankAccountResult deleteAccount(String userId, String bankAccountId) throws EntityNotFoundException {
        User user = findUser(userId);
        BankAccount bankAccount = findBankAccount(bankAccountId, user);

        bankAccount.delete();

        return BankAccountResult.builder()
                .success(true)
                .build();
    }

    private User findUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(
                        userId, EntityType.USER, "user not found"));
    }

    private BankAccount findBankAccount(String bankAccountId, User user) {
        return bankAccountRepository.findByIdAndUserAndDeletedAtIsNull(UUID.fromString(bankAccountId), user)
                .orElseThrow(() -> new EntityNotFoundException(
                        bankAccountId, EntityType.BANK_ACCOUNT, "bank account not found"));
    }
}
