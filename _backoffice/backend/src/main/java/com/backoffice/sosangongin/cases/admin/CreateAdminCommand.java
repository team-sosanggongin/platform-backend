package com.backoffice.sosangongin.cases.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateAdminCommand {
    private final String loginId;
    private final String name;
    private final String phone;
}