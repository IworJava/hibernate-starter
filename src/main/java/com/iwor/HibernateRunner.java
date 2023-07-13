package com.iwor;

import com.iwor.dao.CompanyRepository;
import com.iwor.dao.UserRepository;
import com.iwor.dto.UserCreateDto;
import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.Language;
import com.iwor.entity.Manager;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.mapper.CompanyReadMapper;
import com.iwor.mapper.UserCreateMapper;
import com.iwor.mapper.UserReadMapper;
import com.iwor.service.UserService;
import com.iwor.util.HibernateUtil;
import com.iwor.util.TestDataImporter;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class HibernateRunner {

    public static void main(String[] args) {
        @Cleanup SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
//            dbInit(sessionFactory);

        var session = HibernateUtil.getSessionProxy(sessionFactory);
        session.beginTransaction();


        var companyRepository = new CompanyRepository(session);
        var companyReadMapper = new CompanyReadMapper();
        var userReadMapper = new UserReadMapper(companyReadMapper);
        var userCreateMapper = new UserCreateMapper(companyRepository);
        var userRepository = new UserRepository(session);
        var userService = new UserService(userRepository, userReadMapper, userCreateMapper);

        userService.findById(1L).ifPresent(System.out::println);
        var newUserId = userService.create(new UserCreateDto(
                "ivan@ivan.com",
                PersonalInfo.builder()
                        .firstname("Ivan")
                        .lastname("Ivanov")
                        .birthDate(new Birthday(LocalDate.of(1999, Month.APRIL, 5)))
                        .build(),
                null,
                Role.USER,
                3));
        System.out.println("NEW USER ID: " + newUserId);

        session.getTransaction().commit();
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
