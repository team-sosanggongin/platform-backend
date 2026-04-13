package com.backoffice.sosangongin.role.service;

import com.backoffice.sosangongin.account.service.AccountService;
import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.global.exception.DuplicateResourceException;
import com.backoffice.sosangongin.global.exception.EntityNotFoundException;
import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import com.backoffice.sosangongin.permission.repository.PermissionRepository;
import com.backoffice.sosangongin.role.domain.AccountRole;
import com.backoffice.sosangongin.role.domain.AccountRoleId;
import com.backoffice.sosangongin.role.domain.RoleBackoffice;
import com.backoffice.sosangongin.role.repository.AccountRoleRepository;
import com.backoffice.sosangongin.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountService accountService;

    @Transactional
    public RoleBackoffice create(String roleName, String description, UUID createdBy) {
        if (roleRepository.existsByRoleName(roleName)) {
            throw new DuplicateResourceException("이미 존재하는 role입니다.");
        }
        return roleRepository.save(
                RoleBackoffice.builder()
                        .roleName(roleName)
                        .description(description)
                        .createdBy(createdBy)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<RoleBackoffice> findAll() {
        return roleRepository.findByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public List<RoleBackoffice> findRolesByAccountId(UUID accountId) {
        return accountRoleRepository.findByIdAccountId(accountId).stream()
                .map(AccountRole::getRole)
                .filter(role -> !role.isDeleted())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findPermissionCodesByAccountId(UUID accountId) {
        return findRolesByAccountId(accountId).stream()
                .flatMap(role -> role.getRolePermissions().stream())
                .map(rp -> rp.getPermission().getPermissionName())
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleBackoffice findById(Long id) {
        return roleRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 role입니다."));
    }

    @Transactional
    public RoleBackoffice update(Long id, String roleName, String description) {
        RoleBackoffice role = findById(id);
        role.update(roleName, description);
        return role;
    }

    @Transactional
    public void delete(Long id) {
        RoleBackoffice role = findById(id);
        role.delete();
    }

    @Transactional
    public void updatePermissions(Long roleId, List<Long> permissionIds) {
        RoleBackoffice role = findById(roleId);
        List<PermissionBackoffice> permissions = permissionRepository.findByIdInAndDeletedAtIsNull(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new EntityNotFoundException("존재하지 않는 permission이 포함되어 있습니다.");
        }

        role.replacePermissions(permissions);
    }

    @Transactional
    public void assignRoleToAccount(UUID accountId, Long roleId) {
        BackofficeAdmin account = accountService.findById(accountId);
        RoleBackoffice role = findById(roleId);
        AccountRoleId id = new AccountRoleId(accountId, roleId);
        if (!accountRoleRepository.existsById(id)) {
            accountRoleRepository.save(
                    AccountRole.builder()
                            .id(id)
                            .account(account)
                            .role(role)
                            .build()
            );
        }
    }

    @Transactional
    public void removeRoleFromAccount(UUID accountId, Long roleId) {
        accountRoleRepository.deleteById(new AccountRoleId(accountId, roleId));
    }
}