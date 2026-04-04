package com.backoffice.sosangongin.permission.service;

import com.backoffice.sosangongin.global.exception.DuplicateResourceException;
import com.backoffice.sosangongin.global.exception.EntityNotFoundException;
import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import com.backoffice.sosangongin.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public PermissionBackoffice create(String permissionName, String permDomain) {
        if (permissionRepository.existsByPermissionNameAndPermDomain(permissionName, permDomain)) {
            throw new DuplicateResourceException("이미 존재하는 permission입니다.");
        }
        return permissionRepository.save(
                PermissionBackoffice.builder()
                        .permissionName(permissionName)
                        .permDomain(permDomain)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<PermissionBackoffice> findAll() {
        return permissionRepository.findByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public PermissionBackoffice findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 permission입니다."));
    }

    @Transactional
    public PermissionBackoffice update(Long id, String permissionName, String permDomain) {
        PermissionBackoffice permission = findById(id);
        permission.update(permissionName, permDomain);
        return permission;
    }

    @Transactional
    public void delete(Long id) {
        PermissionBackoffice permission = findById(id);
        permission.delete();
    }
}