package com.iwor;

import com.iwor.converter.BirthdayConverter;
import com.iwor.entity.Birthday;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.configure();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()
        ) {
            session.beginTransaction();

            User user = User.builder()
                    .username("ivan@gmail.com")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                    .role(Role.USER)
                    .build();

            session.delete(user);
            session.save(user);

            session.getTransaction().commit();
        }
    }
}
