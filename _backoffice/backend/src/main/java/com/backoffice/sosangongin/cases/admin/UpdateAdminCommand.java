package com.backoffice.sosangongin.cases.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateAdminCommand {
    private final String name;
    private final String email;
    private final String phone;
}