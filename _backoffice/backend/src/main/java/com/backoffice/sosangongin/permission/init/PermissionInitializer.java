package com.backoffice.sosangongin.permission.init;

import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import com.backoffice.sosangongin.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
public class PermissionInitializer implements ApplicationRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (permissionRepository.count() == 0) {
            permissionRepository.saveAll(List.of(
                    PermissionBackoffice.builder().permissionName("create.notice").permDomain("notice").build(),
                    PermissionBackoffice.builder().permissionName("update.notice").permDomain("notice").build(),
                    PermissionBackoffice.builder().permissionName("delete.notice").permDomain("notice").build(),
                    PermissionBackoffice.builder().permissionName("create.term").permDomain("term").build(),
                    PermissionBackoffice.builder().permissionName("update.term").permDomain("term").build(),
                    PermissionBackoffice.builder().permissionName("delete.term").permDomain("term").build()
            ));
        }
    }
}