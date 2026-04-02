package com.backoffice.sosangongin.controller.dto.admin;

import com.backoffice.sosangongin.cases.admin.UpdateAdminCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAdminRequest {
    private String name;
    private String email;
    private String phone;

    public UpdateAdminCommand toCommand() {
        return UpdateAdminCommand.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
    }
}