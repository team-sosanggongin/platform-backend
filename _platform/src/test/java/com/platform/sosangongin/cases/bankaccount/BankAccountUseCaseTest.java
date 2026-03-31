package com.platform.sosangongin.cases.bankaccount;

import com.platform.sosangongin.domains.bankaccount.BankAccount;
import com.platform.sosangongin.domains.bankaccount.BankAccountRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankAccountUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountUseCase bankAccountUseCase;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .build();
    }

    // ── 조회 ──

    @Test
    @DisplayName("계좌 목록 조회: 등록된 계좌가 있는 경우 목록 반환")
    void getAccounts_HasAccounts() {
        // given
        BankAccount account1 = BankAccount.builder()
                .user(user).bankName("국민은행").accountNumber("123-456-789").accountAlias("급여통장").build();
        BankAccount account2 = BankAccount.builder()
                .user(user).bankName("신한은행").accountNumber("987-654-321").accountAlias(null).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByUserAndDeletedAtIsNull(user)).willReturn(List.of(account1, account2));

        // when
        BankAccountListResult result = bankAccountUseCase.getAccounts(userId.toString());

        // then
        assertThat(result.getAccounts()).hasSize(2);
        assertThat(result.getAccounts().get(0).getBankName()).isEqualTo("국민은행");
        assertThat(result.getAccounts().get(1).getAccountAlias()).isNull();
    }

    @Test
    @DisplayName("계좌 목록 조회: 등록된 계좌가 없는 경우 빈 목록 반환")
    void getAccounts_Empty() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByUserAndDeletedAtIsNull(user)).willReturn(List.of());

        // when
        BankAccountListResult result = bankAccountUseCase.getAccounts(userId.toString());

        // then
        assertThat(result.getAccounts()).isEmpty();
    }

    @Test
    @DisplayName("계좌 목록 조회: 존재하지 않는 사용자인 경우 예외 발생")
    void getAccounts_UserNotFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bankAccountUseCase.getAccounts(userId.toString()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── 등록 ──

    @Test
    @DisplayName("계좌 등록: 대표명 포함하여 등록 성공")
    void createAccount_WithAlias() {
        // given
        BankAccountRequest request = BankAccountRequest.builder()
                .bankName("국민은행")
                .accountNumber("123-456-789")
                .accountAlias("급여통장")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.save(any(BankAccount.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        BankAccountResult result = bankAccountUseCase.createAccount(userId.toString(), request);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAccount().getBankName()).isEqualTo("국민은행");
        assertThat(result.getAccount().getAccountNumber()).isEqualTo("123-456-789");
        assertThat(result.getAccount().getAccountAlias()).isEqualTo("급여통장");

        ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("계좌 등록: 대표명 없이 등록 성공")
    void createAccount_WithoutAlias() {
        // given
        BankAccountRequest request = BankAccountRequest.builder()
                .bankName("신한은행")
                .accountNumber("987-654-321")
                .accountAlias(null)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.save(any(BankAccount.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        BankAccountResult result = bankAccountUseCase.createAccount(userId.toString(), request);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAccount().getAccountAlias()).isNull();
    }

    // ── 수정 ──

    @Test
    @DisplayName("계좌 수정: 은행명, 계좌번호, 대표명 수정 성공")
    void updateAccount_Success() {
        // given
        UUID accountId = UUID.randomUUID();
        BankAccount existingAccount = BankAccount.builder()
                .user(user).bankName("국민은행").accountNumber("111-222-333").accountAlias("기존이름").build();

        BankAccountRequest request = BankAccountRequest.builder()
                .bankName("하나은행")
                .accountNumber("444-555-666")
                .accountAlias("변경이름")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByIdAndUserAndDeletedAtIsNull(accountId, user))
                .willReturn(Optional.of(existingAccount));

        // when
        BankAccountResult result = bankAccountUseCase.updateAccount(userId.toString(), accountId.toString(), request);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAccount().getBankName()).isEqualTo("하나은행");
        assertThat(result.getAccount().getAccountNumber()).isEqualTo("444-555-666");
        assertThat(result.getAccount().getAccountAlias()).isEqualTo("변경이름");
    }

    @Test
    @DisplayName("계좌 수정: 존재하지 않는 계좌인 경우 예외 발생")
    void updateAccount_AccountNotFound() {
        // given
        UUID accountId = UUID.randomUUID();
        BankAccountRequest request = BankAccountRequest.builder()
                .bankName("하나은행")
                .accountNumber("444-555-666")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByIdAndUserAndDeletedAtIsNull(accountId, user))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bankAccountUseCase.updateAccount(userId.toString(), accountId.toString(), request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── 삭제 ──

    @Test
    @DisplayName("계좌 삭제: 소프트 삭제 성공")
    void deleteAccount_Success() {
        // given
        UUID accountId = UUID.randomUUID();
        BankAccount account = spy(BankAccount.builder()
                .user(user).bankName("국민은행").accountNumber("111-222-333").build());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByIdAndUserAndDeletedAtIsNull(accountId, user))
                .willReturn(Optional.of(account));

        // when
        BankAccountResult result = bankAccountUseCase.deleteAccount(userId.toString(), accountId.toString());

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(account).delete();
    }

    @Test
    @DisplayName("계좌 삭제: 존재하지 않는 계좌인 경우 예외 발생")
    void deleteAccount_AccountNotFound() {
        // given
        UUID accountId = UUID.randomUUID();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bankAccountRepository.findByIdAndUserAndDeletedAtIsNull(accountId, user))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bankAccountUseCase.deleteAccount(userId.toString(), accountId.toString()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
