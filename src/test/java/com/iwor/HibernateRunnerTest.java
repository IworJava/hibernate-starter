package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Chat;
import com.iwor.entity.Company;
import com.iwor.entity.Language;
import com.iwor.entity.Manager;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Profile;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.entity.UserChat;
import com.iwor.util.HibernateTestUtil;
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
import java.time.LocalDate;

class HibernateRunnerTest {
    private static SessionFactory sessionFactory;
    private Session session;

    @Test
    void checkHql() {
        Transaction transaction = session.beginTransaction();

        User user = session.createNamedQuery("findUserByName", User.class)
                .setParameter("user", "%ann%")
                .setParameter("company", "apple")
                .uniqueResult();
        System.out.println(user);

        transaction.commit();
    }

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
                    .build();
            userChat.setCreatedAt(Instant.now());
            userChat.setCreatedBy(user.getUsername());
            session.save(userChat);

            transaction.commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (SessionFactory sessionFactory = HibernateTestUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Profile profile = Profile.builder()
                    .street("prospect Mira, 101")
                    .language("ru")
                    .build();
            Programmer user = Programmer.builder()
                    .username("test@gmail.com")
                    .profile(profile)
                    .build();
            session.save(user);

            transaction.commit();
        }
    }

    @BeforeAll
    static void createSessionFactory() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
    }
    @BeforeEach
    void initDb() {
        session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

//        Arrays.stream(SqlQueries.values()).forEachOrdered(sql ->
//                session.createSQLQuery(sql.getQuery()).executeUpdate());

        Company company1 = Company.builder().name("Apple").build();
        Company company2 = Company.builder().name("Google").build();
        Company company3 = Company.builder().name("Amazon").build();
        session.persist(company1);
        session.persist(company2);
        session.persist(company3);

        PersonalInfo personalInfo1 = PersonalInfo.builder().firstname("Ivan").lastname("Ivanov").birthDate(new Birthday(LocalDate.of(2000, 10, 20))).build();
        PersonalInfo personalInfo2 = PersonalInfo.builder().firstname("Petr").lastname("Petrov").birthDate(new Birthday(LocalDate.of(1990, 9, 19))).build();
        PersonalInfo personalInfo3 = PersonalInfo.builder().firstname("Anna").lastname("Volkova").birthDate(new Birthday(LocalDate.of(1980, 8, 18))).build();
        Programmer programmer1 = Programmer.builder()
                .username("ivan@gmail.com")
                .personalInfo(personalInfo1)
                .role(Role.USER)
                .info("{\"id\": 1, \"name\": \"Ivan\"}")
                .company(company1)
                .language(Language.PYTHON)
                .build();
        Manager manager = Manager.builder()
                .username("petr@gmail.com")
                .personalInfo(personalInfo2)
                .role(Role.USER)
                .info("{\"id\": 2, \"name\": \"Petr\"}")
                .company(company1)
                .projectName("Mega Project")
                .build();
        Programmer programmer2 = Programmer.builder()
                .username("anna@gmail.com")
                .personalInfo(personalInfo3)
                .role(Role.ADMIN)
                .info("{\"id\": 3, \"name\": \"Anna\"}")
                .company(company1)
                .language(Language.JAVA)
                .build();
        session.persist(programmer1);
        session.persist(manager);
        session.persist(programmer2);

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
