package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.business.BusinessStatus;
import com.platform.sosangongin.domains.employment.Employment;
import com.platform.sosangongin.domains.employment.EmploymentRole;
import com.platform.sosangongin.domains.employment.EmploymentStatus;
import com.platform.sosangongin.domains.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class BusinessRoleTest {

    @Test
    @DisplayName("유저는 사업체와 연관되며, 하나 이상의 롤을 가지고 있다")
    void user_business_mappings(){

        User owner = User.builder()
                .id(UUID.randomUUID())
                .phoneNumber("01099321967")
                .build();

        Business business = Business.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .status(BusinessStatus.ACTIVE)
                .build();

        RolePermission permission = RolePermission.builder()
                .permissionId(1L)
                .permDomain(new PermissionDomain("*"))
                .build();

        BusinessRole businessRole = BusinessRole.builder()
                .id(1L)
                .roleName("manager")
                .business(business)
                .rolePermissionSet(Set.of(permission))
                .build();

        User employee = User.builder()
                .id(UUID.randomUUID())
                .phoneNumber("01099311967")
                .build();

        Employment employment = Employment.builder()
                .business(business)
                .id(1L)
                .status(EmploymentStatus.ACTIVE)
                .user(employee)
                .build();

        EmploymentRole roles = EmploymentRole.builder()
                .employment(employment)
                .id(1L)
                .role(businessRole)
                .build();

        





    }

}