package com.iwor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity<T extends Serializable> implements BaseEntity<T> {

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 128)
    private String createdBy;

}
