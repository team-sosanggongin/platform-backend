package com.backoffice.sosangongin.auth.init;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.auth.repos.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RootAdminInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!adminRepository.existsByLoginId("root")) {
            adminRepository.save(
                    BackofficeAdmin.builder()
                            .loginId("root")
                            .password(passwordEncoder.encode("root"))
                            .name("root")
                            .isRoot(true)
                            .isPasswordExpired(false)
                            .build()
            );
        }
    }
}