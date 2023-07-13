package com.iwor.util;

import com.iwor.HibernateRunner;
import com.iwor.entity.Audit;
import com.iwor.entity.Revision;
import com.iwor.interceptor.GlobalInterceptor;
import com.iwor.listener.AuditTableListener;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;

import java.lang.reflect.Proxy;

@UtilityClass
public class HibernateUtil {

    public static Session getSessionProxy(SessionFactory sessionFactory) {
        return (Session) Proxy.newProxyInstance(
                HibernateRunner.class.getClassLoader(),
                new Class[]{Session.class},
                (proxy, method, args1) -> method.invoke(sessionFactory.getCurrentSession(), args1)
        );
    }

    public static SessionFactory buildSessionFactory() {
        var sessionFactory = buildConfiguration().buildSessionFactory();
//        registerListeners(sessionFactory);
        return sessionFactory;
    }

    private static void registerListeners(SessionFactory sessionFactory) {
        var sessionFactoryImpl = sessionFactory.unwrap(SessionFactoryImpl.class);
        var listenerRegistry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
        var auditTableListener = new AuditTableListener();
        listenerRegistry.appendListeners(EventType.POST_INSERT, auditTableListener);
        listenerRegistry.appendListeners(EventType.PRE_UPDATE, auditTableListener);
        listenerRegistry.appendListeners(EventType.PRE_DELETE, auditTableListener);
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.addAnnotatedClass(Audit.class);
        configuration.addAnnotatedClass(Revision.class);
        configuration.setInterceptor(new GlobalInterceptor());
        return configuration.configure();
    }
}
