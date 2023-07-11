package com.iwor.listener;

import com.iwor.entity.Revision;
import org.hibernate.envers.RevisionListener;

public class IwRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        ((Revision) revisionEntity).setUserName("iw");
    }
}
