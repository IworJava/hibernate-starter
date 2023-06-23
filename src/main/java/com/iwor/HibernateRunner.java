package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;

@Slf4j
public class HibernateRunner {
    private static final User USER = User.builder()
            .username("ivan1@gmail.com")
            .firstname("Ivan")
            .lastname("Ivanov")
            .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
            .role(Role.USER)
            .info("""
                    {
                        "name": "Ivan",
                        "id": 25
                    }
                    """)
            .build();

    public static void main(String[] args) {
        log.info("User entity is in the transient state, object: {}", USER);

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session = sessionFactory.openSession();
            try (session) {
                Transaction transaction = session.beginTransaction();
                log.trace("Transaction is created, transaction: {}", transaction);

//                session.merge(USER);
                log.trace("User is in the persistent state: {}, session: {}", USER, session);

                session.getTransaction().commit();
            }
            log.warn("User is in the detached state: {}, session is closed: {}", USER, session);

        } catch (Exception e) {
            log.error("An exception occurred", e);
            throw e;
        }
    }
}
