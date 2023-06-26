package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.PersonalInfo;
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
    private static final Company COMPANY = new Company(null, "Google");
    private static final PersonalInfo PERSONAL_INFO = PersonalInfo.builder()
            .firstname("Petr")
            .lastname("Petrov")
            .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
            .build();
    private static final User USER = User.builder()
            .username("petr1@gmail.com")
            .personalInfo(PERSONAL_INFO)
            .role(Role.USER)
            .info("{\"id\": 25, \"name\": \"Ivan\"}")
//            .company(COMPANY)
            .build();

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session = sessionFactory.openSession();
            try (session) {
                Transaction transaction = session.beginTransaction();

//                session.save(COMPANY);
//                session.save(USER);

                User user = session.get(User.class, 2L);
                System.out.println(user);

                transaction.commit();
            }
        }
    }
}
