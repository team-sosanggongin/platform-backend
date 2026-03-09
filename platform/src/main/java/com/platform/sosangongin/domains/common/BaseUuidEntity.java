package com.platform.sosangongin.domains.common;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseUuidEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
