package com.iwor;

import com.iwor.entity.Birthday;
import com.iwor.entity.Company;
import com.iwor.entity.Language;
import com.iwor.entity.Manager;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.QueryHints;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
public class HibernateRunner {
    private static final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    public static void main(String[] args) {
        try (sessionFactory) {
//            TestDataImporter.importData(sessionFactory);
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();

//                session.enableFetchProfile("withCompany");
//                session.enableFetchProfile("withCompanyAndPayments");
//                var user = session.get(User.class, 1L);

//                Map<String, Object> properties = Map.of(
//                        GraphSemantic.LOAD.getJpaHintName(),
//                        session.getEntityGraph("withCompanyAndChats")
//                );
//                var user = session.find(User.class, 1L, properties);
//                System.out.println(user.getCompany().getName());
//                System.out.println(user.getUserChats().size());

                var userGraph = session.createEntityGraph(User.class);
                userGraph.addAttributeNodes("company", "userChats");
                userGraph.addSubgraph("userChats")
                        .addAttributeNodes("chat");

                var users = session.createQuery("SELECT u FROM User u", User.class)
                        .setHint(
                                GraphSemantic.FETCH.getJpaHintName(),
//                                session.getEntityGraph("withCompanyAndChats")
                                userGraph
                        )
                        .list();
                users.forEach(u -> System.out.println(u.getCompany().getName()));
                users.forEach(u -> System.out.println(u.getUserChats().size()));
//                users.forEach(u -> u.getUserChats().forEach(uc -> System.out.println(uc.getChat().getName())));

                transaction.commit();
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
