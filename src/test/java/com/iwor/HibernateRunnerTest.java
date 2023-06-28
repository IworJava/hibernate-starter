package com.iwor;

import com.iwor.entity.Chat;
import com.iwor.entity.Company;
import com.iwor.entity.Profile;
import com.iwor.entity.User;
import com.iwor.entity.UserChat;
import com.iwor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class HibernateRunnerTest {
    private static final String DROP_ALL_SQL = """
            DROP TABLE IF EXISTS profile,
                                 users,
                                 company_locale,
                                 company;
            """;
    private static final String CREATE_COMPANY_SQL = """
            CREATE TABLE company (
                id SERIAL PRIMARY KEY,
                name VARCHAR(64) NOT NULL UNIQUE
            );
            """;
    private static final String CREATE_COMPANY_LOCALE_SQL = """
            CREATE TABLE company_locale (
                company_id INT NOT NULL REFERENCES company (id),
                lang CHAR(2) NOT NULL,
                description VARCHAR(128) NOT NULL,
                PRIMARY KEY (company_id, lang)
            );
            """;
    private static final String CREATE_PROFILE_SQL = """
            CREATE TABLE profile (
                id BIGSERIAL PRIMARY KEY,
                street VARCHAR(128),
                language CHAR(2)
            );
            """;
    private static final String CREATE_USERS_SQL = """
            CREATE TABLE users (
                id BIGSERIAL PRIMARY KEY,
                username VARCHAR(128) NOT NULL UNIQUE,
                firstname VARCHAR(128),
                lastname VARCHAR(128),
                birth_date DATE,
                role VARCHAR(1),
                info JSONB,
                company_id INT NULL REFERENCES company (id),
                profile_id BIGINT UNIQUE REFERENCES profile (id)
            );
            """;
    private static SessionFactory sessionFactory;
    private Session session;

    @Test
    void checkOrdering() {
        session = sessionFactory.openSession();
        session.beginTransaction();

        Company company1 = session.get(Company.class, 1);
        company1.getUsers().forEach((k, v) -> System.out.println(v));

        Company company3 = session.get(Company.class, 3);
        company3.getLocales().forEach((k, v) -> System.out.printf("%s: %s%n", k, v));

        session.getTransaction().commit();
    }

    @Test
    void checkLocaleInfo() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Company company = session.get(Company.class, 3);
            company.getLocales().put("ru", "Описание на русском");
            company.getLocales().put("en", "English description");

            transaction.commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, 3L);
            Chat chat = session.get(Chat.class, 4L);
            UserChat userChat = UserChat.builder()
                    .user(user)
                    .chat(chat)
                    .createdAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();
            session.save(userChat);

            transaction.commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Profile profile = Profile.builder()
                    .street("prospect Mira, 101")
                    .language("ru")
                    .build();
            User user = User.builder()
                    .username("test@gmail.com")
                    .profile(profile)
                    .build();
            session.save(user);

            transaction.commit();
        }
    }

    @BeforeAll
    static void createSessionFactory() {
        sessionFactory = HibernateUtil.buildSessionFactory();
    }
    @BeforeEach
    void initDb() {
        session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.createSQLQuery(DROP_ALL_SQL).executeUpdate();
        session.createSQLQuery(CREATE_COMPANY_SQL).executeUpdate();
        session.createSQLQuery(CREATE_COMPANY_LOCALE_SQL).executeUpdate();
        session.createSQLQuery(CREATE_PROFILE_SQL).executeUpdate();
        session.createSQLQuery(CREATE_USERS_SQL).executeUpdate();

        Company company1 = Company.builder().name("Apple").build();
        Company company2 = Company.builder().name("Google").build();
        Company company3 = Company.builder().name("Amazon").build();
        session.persist(company1);
        session.persist(company2);
        session.persist(company3);

        User user1 = User.builder().username("ivan@gmail.com").company(company1).build();
        User user2 = User.builder().username("petr@gmail.com").company(company1).build();
        User user3 = User.builder().username("anna@gmail.com").company(company1).build();
        session.persist(user1);
        session.persist(user2);
        session.persist(user3);

        company3.getLocales().put("ru", "Описание на русском");
        company3.getLocales().put("en", "English description");

        transaction.commit();
    }
    @AfterEach
    void closeSession() {
        if (session != null) {
            session.close();
        }
    }
    @AfterAll
    static void close() {
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }
}