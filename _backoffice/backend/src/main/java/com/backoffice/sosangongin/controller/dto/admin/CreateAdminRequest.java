package com.backoffice.sosangongin.controller.dto.admin;

import com.backoffice.sosangongin.cases.admin.CreateAdminCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAdminRequest {
    private String loginId;
    private String name;
    private String phone;

    public CreateAdminCommand toCommand() {
        return CreateAdminCommand.builder()
                .loginId(loginId)
                .name(name)
                .phone(phone)
                .build();
    }
}