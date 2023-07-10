package com.iwor.listener;

import com.iwor.entity.AuditableEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

public class AuditDateListener {

    @PrePersist
    public void prePersist(AuditableEntity<?> entity) {
        entity.setCreatedAt(Instant.now());
//        setCreatedBy(SecurityContext.getUser());
    }

    @PreUpdate
    public void preUpdate(AuditableEntity<?> entity) {
        entity.setUpdatedAt(Instant.now());
//        setUpdatedBy(SecurityContext.getUser());
    }
}
