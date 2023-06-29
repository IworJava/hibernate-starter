package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.util.HibernateUtil;
import com.iwor.util.SqlQueries;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Arrays;

@Slf4j
public class HibernateRunner {
    private static final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    public static void main(String[] args) {
        try (sessionFactory) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

                dbInit();

                Company company = session.get(Company.class, 1);
                company.getUsers().forEach((k, v) -> System.out.println(v));

                transaction.commit();
            }
        }
    }

    private static void dbInit() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Arrays.stream(SqlQueries.values()).forEachOrdered(sql ->
                    session.createSQLQuery(sql.getQuery()).executeUpdate());

            Company company1 = Company.builder().name("Apple").build();
            Company company2 = Company.builder().name("Google").build();
            Company company3 = Company.builder().name("Amazon").build();
            session.persist(company1);
            session.persist(company2);
            session.persist(company3);

            PersonalInfo personalInfo1 = PersonalInfo.builder().firstname("Ivan").lastname("Ivanov").birthDate(new Birthday(LocalDate.of(2000, 10, 20))).build();
            PersonalInfo personalInfo2 = PersonalInfo.builder().firstname("Petr").lastname("Petrov").birthDate(new Birthday(LocalDate.of(1990, 9, 19))).build();
            PersonalInfo personalInfo3 = PersonalInfo.builder().firstname("Anna").lastname("Volkova").birthDate(new Birthday(LocalDate.of(1980, 8, 18))).build();
            User user1 = User.builder().username("ivan@gmail.com").personalInfo(personalInfo1).role(Role.USER).info("{\"id\": 1, \"name\": \"Ivan\"}").company(company1).build();
            User user2 = User.builder().username("petr@gmail.com").personalInfo(personalInfo2).role(Role.USER).info("{\"id\": 2, \"name\": \"Petr\"}").company(company1).build();
            User user3 = User.builder().username("anna@gmail.com").personalInfo(personalInfo3).role(Role.ADMIN).info("{\"id\": 3, \"name\": \"Anna\"}").company(company1).build();
            session.persist(user1);
            session.persist(user2);
            session.persist(user3);

            company3.getLocales().put("ru", "Описание на русском");
            company3.getLocales().put("en", "English description");

            session.getTransaction().commit();
        }
    }
}
