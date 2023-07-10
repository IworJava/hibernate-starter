package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.Language;
import com.iwor.entity.Manager;
import com.iwor.entity.Payment;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.LockModeType;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {
    private static final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();

    public static void main(String[] args) {
        try (sessionFactory) {
//            TestDataImporter.importData(sessionFactory);

            try (var session = sessionFactory.openSession();
                 var session1 = sessionFactory.openSession();
            ) {
                session.beginTransaction();
//                session1.beginTransaction();

                var payment = session.find(Payment.class, 1L
                        , LockModeType.OPTIMISTIC_FORCE_INCREMENT
//                        , Map.of(READ_ONLY, true)
                );
                payment.setAmount(payment.getAmount() + 30);

//                var payment1 = session1.find(Payment.class, 1L
//                        , LockModeType.PESSIMISTIC_READ
//                );
//                payment1.setAmount(payment.getAmount() + 10);

                session.getTransaction().commit();
//                session1.getTransaction().commit();
            }
        }
    }

    private static void dbInit(Session session) {
//        Transaction transaction = session.beginTransaction();
//        Arrays.stream(SqlQueries.values()).forEachOrdered(sql ->
//                session.createSQLQuery(sql.getQuery()).executeUpdate());

//        Company company1 = Company.builder().name("Apple").build();
//        Company company2 = Company.builder().name("Google").build();
//        Company company3 = Company.builder().name("Amazon").build();
//        session.persist(company1);
//        session.persist(company2);
//        session.persist(company3);

        PersonalInfo personalInfo1 = PersonalInfo.builder().firstname("Ivan").lastname("Ivanov").birthDate(new Birthday(LocalDate.of(2000, 10, 20))).build();
        PersonalInfo personalInfo2 = PersonalInfo.builder().firstname("Petr").lastname("Petrov").birthDate(new Birthday(LocalDate.of(1990, 9, 19))).build();
        PersonalInfo personalInfo3 = PersonalInfo.builder().firstname("Anna").lastname("Volkova").birthDate(new Birthday(LocalDate.of(1980, 8, 18))).build();

        var company1 = session.get(Company.class, 2);

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

//        company3.getLocales().put("ru", "Описание на русском");
//        company3.getLocales().put("en", "English description");

//        transaction.commit();
    }
}
