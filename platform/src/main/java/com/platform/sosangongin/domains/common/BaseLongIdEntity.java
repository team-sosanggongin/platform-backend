package com.platform.sosangongin.domains.common;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseLongIdEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
