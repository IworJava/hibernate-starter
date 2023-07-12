package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.Language;
import com.iwor.entity.Manager;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.entity.UserChat;
import com.iwor.util.HibernateUtil;
import com.iwor.util.TestDataImporter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.time.LocalDate;

@Slf4j
public class HibernateRunner {
    private static final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();

    public static void main(String[] args) {
        try (sessionFactory) {
//            dbInit(sessionFactory);

            User user = null;
            try (var session = sessionFactory.openSession()) {
                session.beginTransaction();

                user = session.get(User.class, 1L);
                var user1 = session.get(User.class, 1L);
                user.getCompany().getName();
                user.getUserChats().size();

                session.getTransaction().commit();
            }

            try (var session1 = sessionFactory.openSession()) {
                session1.beginTransaction();

                user = session1.get(User.class, 1L);
                user.getCompany().getName();
                user.getUserChats().size();
                session1.get(UserChat.class, 1L);

                session1.getTransaction().commit();
            }
        }
    }

    private static void dbInit(SessionFactory sessionFactory) {
        TestDataImporter.importData(sessionFactory);

        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();

//            Arrays.stream(SqlQueries.values()).forEachOrdered(sql ->
//                    session.createSQLQuery(sql.getQuery()).executeUpdate());
//
//            Company company1 = Company.builder().name("Apple").build();
//            Company company2 = Company.builder().name("Google").build();
//            Company company3 = Company.builder().name("Amazon").build();
//            session.persist(company1);
//            session.persist(company2);
//            session.persist(company3);

            PersonalInfo personalInfo1 = PersonalInfo.builder().firstname("Ivan").lastname("Ivanov").birthDate(new Birthday(LocalDate.of(2000, 10, 20))).build();
            PersonalInfo personalInfo2 = PersonalInfo.builder().firstname("Petr").lastname("Petrov").birthDate(new Birthday(LocalDate.of(1990, 9, 19))).build();
            PersonalInfo personalInfo3 = PersonalInfo.builder().firstname("Anna").lastname("Volkova").birthDate(new Birthday(LocalDate.of(1980, 8, 18))).build();

            var company2 = session.get(Company.class, 2);
            var company3 = session.get(Company.class, 3);

            Programmer programmer1 = Programmer.builder()
                    .username("ivan@gmail.com")
                    .personalInfo(personalInfo1)
                    .role(Role.USER)
                    .info("{\"id\": 1, \"name\": \"Ivan\"}")
                    .company(company2)
                    .language(Language.PYTHON)
                    .build();
            Manager manager = Manager.builder()
                    .username("petr@gmail.com")
                    .personalInfo(personalInfo2)
                    .role(Role.USER)
                    .info("{\"id\": 2, \"name\": \"Petr\"}")
                    .company(company2)
                    .projectName("Mega Project")
                    .build();
            Programmer programmer2 = Programmer.builder()
                    .username("anna@gmail.com")
                    .personalInfo(personalInfo3)
                    .role(Role.ADMIN)
                    .info("{\"id\": 3, \"name\": \"Anna\"}")
                    .company(company2)
                    .language(Language.JAVA)
                    .build();
            session.persist(programmer1);
            session.persist(manager);
            session.persist(programmer2);

            company3.getLocales().put("ru", "Описание на русском");
            company3.getLocales().put("en", "English description");

            session.getTransaction().commit();
        }
    }
}
