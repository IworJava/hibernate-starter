package com.iwor.listener;

import com.iwor.entity.Audit;
import com.iwor.entity.Audit.Operation;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serial;

public class AuditTableListener implements
        PostInsertEventListener,
        PreUpdateEventListener,
        PreDeleteEventListener {

    @Serial
    private static final long serialVersionUID = -8046672732908470708L;

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity().getClass() != Audit.class) {
            event.getSession().save(
                    Audit.builder()
                            .entityId(event.getId())
                            .entityName(event.getEntity().getClass().getName())
                            .entityContent(event.getEntity().toString())
                            .operation(Operation.INSERT)
                            .build()
            );
        }
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        extracted(event, Operation.UPDATE);
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        extracted(event, Operation.DELETE);
        return false;
    }

    private static void extracted(AbstractPreDatabaseOperationEvent event, Operation operation) {
        event.getSession().save(
                Audit.builder()
                        .entityId(event.getId())
                        .entityName(event.getEntity().getClass().getName())
                        .entityContent(event.getEntity().toString())
                        .operation(operation)
                        .build()
        );
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
