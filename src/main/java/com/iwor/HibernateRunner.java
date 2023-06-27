package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.User;
import com.iwor.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;

@Slf4j
public class HibernateRunner {
    public static void main(String[] args) {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

//                Profile profile = session.get(Profile.class, 3L);
                User user = session.get(User.class, 3L);

                transaction.commit();
            }
        }
    }

    private static void addUsers(Session session, Integer companyId) {
        User user1 = User.builder().username("ivan@gmail.com").build();
        User user2 = User.builder().username("petr@gmail.com").build();
        User user3 = User.builder().username("anna@gmail.com").build();
        Company company = session.get(Company.class, companyId);
        company.addUser(user1, user2, user3);
        session.merge(company);
    }

    private static PersonalInfo getPersonalInfo() {
        return PersonalInfo.builder()
                .firstname("Petr")
                .lastname("Petrov")
                .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                .build();
    }

    private static String getJson() {
        return "{\"id\": 25, \"name\": \"Ivan\"}";
    }

    private static void addCompanies(Session session) {
        Company company1 = Company.builder().name("Apple").build();
        Company company2 = Company.builder().name("Google").build();
        Company company3 = Company.builder().name("Amazon").build();
        session.persist(company1);
        session.persist(company2);
        session.persist(company3);
    }
}
