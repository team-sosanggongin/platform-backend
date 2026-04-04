package com.backoffice.sosangongin.role.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountRoleId implements Serializable {
    private UUID accountId;
    private Long roleId;
}