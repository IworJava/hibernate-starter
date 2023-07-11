package com.iwor.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serial;
import java.io.Serializable;

public class GlobalInterceptor extends EmptyInterceptor {
    @Serial
    private static final long serialVersionUID = -6368688611753977031L;

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
