package com.epam.esm.listener;

import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class AuditListener {
    private enum AuditOperation {
        INSERT, UPDATE, DELETE
    }

    @PostPersist
    public void onPostPersist(Object entity) {
        audit(AuditOperation.INSERT, entity);
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        audit(AuditOperation.UPDATE, entity);
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        audit(AuditOperation.DELETE, entity);
    }

    private void audit(AuditOperation operation, Object entity) {
        LocalDateTime timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        log.info("Operation {} on {} was made at {}", operation, entity, timestamp);
    }
}
