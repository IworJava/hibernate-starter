package com.iwor.dao;

import com.iwor.dto.PaymentFilter;
import com.iwor.entity.Payment;
import com.iwor.entity.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.List;

import static com.iwor.entity.QCompany.company;
import static com.iwor.entity.QPayment.payment;
import static com.iwor.entity.QUser.user;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
//        return session.createQuery("SELECT u FROM User u", User.class)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//        var user = criteria.from(User.class);
//
//        criteria.select(user);
//
//        return session.createQuery(criteria).list();
//        ======================================================================
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
//        return session.createQuery(
//                "SELECT u FROM User u " +
//                "WHERE u.personalInfo.firstname = :firstName", User.class)
//                .setParameter("firstName", firstName)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//        var user = criteria.from(User.class);
//
//        var select = criteria.select(user);
//        criteria.where(
//                cb.equal(user.get(User_.personalInfo).get("firstname"), firstName)
//        );
//
//        return session.createQuery(criteria).list();
//        ======================================================================
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo.firstname.eq(firstName))
                .fetch();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        return session.createQuery(
//                "SELECT u FROM User u " +
//                "ORDER BY u.personalInfo.birthDate", User.class)
//                .setMaxResults(limit)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//        var user = criteria.from(User.class);
//
//        criteria.select(user)
//                .orderBy(
//                        cb.asc(user.get(User_.personalInfo).get("birthDate"))
//                );
//
//        return session.createQuery(criteria)
//                .setMaxResults(limit)
//                .list();
//        ======================================================================
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .limit(limit)
                .orderBy(user.personalInfo.birthDate.asc())
                .fetch();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
//        return session.createQuery(
//                "SELECT u FROM User u " +
//                "JOIN u.company c " +
//                "WHERE c.name = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//
//        var company = criteria.from(Company.class);
//        var users = company.join(Company_.users);
//
//        criteria.select(users)
//                .where(
//                        cb.equal(company.get(Company_.name), companyName)
//                );
//
//        return session.createQuery(criteria).list();
//        ======================================================================
        return new JPAQuery<User>(session)
                .select(user)
                .from(company)
                .join(company.users, user)
                .where(company.name.eq(companyName))
                .fetch();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
//        return session.createQuery(
//                "SELECT p FROM Payment p " +
//                "JOIN p.receiver u " +
//                "JOIN u.company c " +
//                "WHERE c.name = :companyName " +
//                "ORDER BY u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(Payment.class);
//
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//        var company = user.join(User_.company);
//
//        criteria.select(payment)
//                .where(
//                        cb.equal(company.get(Company_.name), companyName)
//                )
//                .orderBy(
//                        cb.asc(user.get(User_.personalInfo).get("firstname")),
//                        cb.asc(payment.get(Payment_.amount))
//                );
//
//        return session.createQuery(criteria).list();
//        ======================================================================
        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(payment)
                .join(payment.receiver, user)
                .join(user.company, company)
                .where(company.name.eq(companyName))
                .orderBy(
                        user.personalInfo.firstname.asc(),
                        payment.amount.asc()
                )
                .fetch();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, PaymentFilter filter) {
//        return session.createQuery(
//                "SELECT AVG(p.amount) " +
//                "FROM Payment p " +
//                "JOIN p.receiver u " +
//                "WHERE u.personalInfo.firstname = :firstName " +
//                "AND u.personalInfo.lastname = :lastName", Double.class)
//                .setParameter("firstName", firstName)
//                .setParameter("lastName", lastName)
//                .getSingleResult();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(Double.class);
//
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//
//        List<Predicate> predicates = new ArrayList<>();
//        if (firstName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get("firstname"), firstName));
//        }
//        if (lastName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get("lastname"), lastName));
//        }
//
//        criteria.select(cb.avg(payment.get(Payment_.amount)))
//                .where(predicates.toArray(Predicate[]::new));
//
//        return session.createQuery(criteria).uniqueResult();
//        ======================================================================
        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(payment)
                .join(payment.receiver, user)
                .where(QPredicate.builder()
                        .add(filter.getFirstname(), user.personalInfo.firstname::eq)
                        .add(filter.getLastname(), user.personalInfo.lastname::eq)
                        .buildAnd()
                )
                .fetchOne();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
//        return session.createQuery(
//                "SELECT c.name, AVG(p.amount) " +
//                "FROM Company c " +
//                "JOIN c.users u " +
//                "JOIN u.payments p " +
//                "GROUP BY c.name " +
//                "ORDER BY c.name", Object[].class)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(CompanyDto.class);
//
//        var company = criteria.from(Company.class);
//        var user = company.join(Company_.users, JoinType.INNER);
//        var payment = user.join(User_.payments);
//
//        criteria.select(
//                cb.construct(CompanyDto.class,
//                        company.get(Company_.name),
//                        cb.avg(payment.get(Payment_.amount))
//                )
//        )
//                .groupBy(company)
//                .orderBy(cb.asc(company.get(Company_.name)));
//
//        return session.createQuery(criteria).list();
//        ======================================================================
        return new JPAQuery<Tuple>(session)
                .select(company.name, payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .fetch();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Tuple> isItPossible(Session session) {
//        return session.createQuery(
//                "SELECT u, AVG(p.amount) " +
//                "FROM User u " +
//                "JOIN u.payments p " +
//                "GROUP BY u " +
//                "HAVING AVG(p.amount) > (SELECT AVG(p.amount) FROM Payment p) " +
//                "ORDER BY u.personalInfo.firstname", Object[].class)
//                .list();
//        ======================================================================
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(Tuple.class);
//
//        var user = criteria.from(User.class);
//        var payment = user.join(User_.payments);
//
//        var subquery = criteria.subquery(Double.class);
//        var paymentSub = subquery.from(Payment.class);
//
//        criteria.select(cb.tuple(user, cb.avg(payment.get(Payment_.amount))))
//                .groupBy(user)
//                .having(cb.gt(
//                        cb.avg(payment.get(Payment_.amount)),
//                        subquery.select(cb.avg(paymentSub.get(Payment_.amount)))
//                        ))
//                .orderBy(cb.asc(user.get(User_.personalInfo).get("firstname")));
//
//        return session.createQuery(criteria).list();
//        ======================================================================
return new JPAQuery<Tuple>(session)
        .select(
                user,
                payment.amount.avg()
        )
        .from(user)
        .join(user.payments, payment)
        .groupBy(user)
        .having(payment.amount.avg().gt(
                new JPAQuery<Double>(session)
                        .select(payment.amount.avg())
                        .from(payment)
        ))
        .orderBy(user.personalInfo.firstname.asc())
        .fetch();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}