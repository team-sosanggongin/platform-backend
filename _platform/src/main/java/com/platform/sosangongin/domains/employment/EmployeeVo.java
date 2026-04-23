package com.platform.sosangongin.domains.employment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class EmployeeVo {
    private final Long employmentId;
    private final UUID userId;
    private final String userName;
    private final String phoneNumber;
    private final EmploymentStatus status;
    private final List<EmployeeRoleVo> roles;
    private final LocalDateTime hiredAt;

    public static EmployeeVo from(Employment employment, List<EmploymentRole> employmentRoles) {
        List<EmployeeRoleVo> roles = employmentRoles.stream()
                .map(er -> EmployeeRoleVo.from(er.getRole()))
                .toList();

        return EmployeeVo.builder()
                .employmentId(employment.getId())
                .userId(employment.getUser().getId())
                .userName(employment.getUser().getName())
                .phoneNumber(employment.getUser().getPhoneNumber())
                .status(employment.getStatus())
                .roles(roles)
                .hiredAt(employment.getCreatedAt())
                .build();
    }
}