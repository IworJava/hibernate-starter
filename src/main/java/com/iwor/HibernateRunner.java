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
import com.iwor.interceptor.TransactionInterceptor;
import com.iwor.mapper.CompanyReadMapper;
import com.iwor.mapper.UserCreateMapper;
import com.iwor.mapper.UserReadMapper;
import com.iwor.service.UserService;
import com.iwor.util.HibernateUtil;
import com.iwor.util.TestDataImporter;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.SessionFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        @Cleanup SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
//            dbInit(sessionFactory);

        var session = HibernateUtil.getSessionProxy(sessionFactory);
        session.beginTransaction();


        var companyRepository = new CompanyRepository(session);
        var companyReadMapper = new CompanyReadMapper();
        var userReadMapper = new UserReadMapper(companyReadMapper);
        var userCreateMapper = new UserCreateMapper(companyRepository);
        var userRepository = new UserRepository(session);
//        var userService = new UserService(userRepository, userReadMapper, userCreateMapper);
        var transactionInterceptor = new TransactionInterceptor(sessionFactory);

        UserService userService = new ByteBuddy()
                .subclass(UserService.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(transactionInterceptor))
                .make()
                .load(UserService.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor(UserRepository.class, UserReadMapper.class, UserCreateMapper.class)
                .newInstance(userRepository, userReadMapper, userCreateMapper);

        userService.findById(1L).ifPresent(System.out::println);
            var newUserId = userService.create(new UserCreateDto(
//                null,
                "ivan2@ivan.com",
                PersonalInfo.builder()
                        .firstname("Ivan")
                        .lastname("Ivanov")
//                        .birthDate(new Birthday(LocalDate.of(1988, Month.APRIL, 5)))
                        .build(),
                null,
//                Role.USER,
                null,
                1));
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
