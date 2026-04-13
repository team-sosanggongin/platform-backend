package com.backoffice.sosangongin.account.usecase;

import com.backoffice.sosangongin.account.dto.*;
import com.backoffice.sosangongin.account.service.AccountService;
import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.role.dto.RoleSummary;
import com.backoffice.sosangongin.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountService accountService;
    private final RoleService roleService;

    public AccountResponse create(AccountCreateRequest request) {
        return AccountResponse.from(accountService.create(request));
    }

    public List<AccountResponse> findAll() {
        return accountService.findAll().stream()
                .map(admin -> {
                    List<RoleSummary> roles = roleService.findRolesByAccountId(admin.getId()).stream()
                            .map(RoleSummary::from)
                            .toList();
                    return AccountResponse.from(admin, roles);
                })
                .toList();
    }

    public AccountResponse findById(UUID id) {
        return buildWithRolesAndPermissions(accountService.findById(id));
    }

    public AccountResponse getMe(UUID id) {
        return buildWithRolesAndPermissions(accountService.findById(id));
    }

    public AccountResponse update(UUID id, AccountUpdateRequest request) {
        return AccountResponse.from(accountService.update(id, request));
    }

    public void delete(UUID id) {
        accountService.delete(id);
    }

    public void unlock(UUID id) {
        accountService.unlock(id);
    }

    public void changePassword(UUID requesterId, PasswordChangeRequest request) {
        accountService.changePassword(requesterId, request);
    }

    private AccountResponse buildWithRolesAndPermissions(BackofficeAdmin admin) {
        List<RoleSummary> roles = roleService.findRolesByAccountId(admin.getId()).stream()
                .map(RoleSummary::from)
                .toList();
        List<String> permissionCodes = roleService.findPermissionCodesByAccountId(admin.getId());
        return AccountResponse.from(admin, roles, permissionCodes);
    }
}
