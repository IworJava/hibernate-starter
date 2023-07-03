package com.iwor.dao;

import com.iwor.entity.Payment;
import com.iwor.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
        return session.createQuery("SELECT u FROM User u", User.class)
                .list();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
        return session.createQuery(
                "SELECT u FROM User u " +
                "WHERE u.personalInfo.firstname = :firstName", User.class)
                .setParameter("firstName", firstName)
                .list();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return session.createQuery(
                "SELECT u FROM User u " +
                "ORDER BY u.personalInfo.birthDate", User.class)
                .setMaxResults(limit)
                .list();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return session.createQuery(
                "SELECT u FROM User u " +
                "JOIN u.company c " +
                "WHERE c.name = :companyName", User.class)
                .setParameter("companyName", companyName)
                .list();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return session.createQuery(
                "SELECT p FROM Payment p " +
                "JOIN p.receiver u " +
                "JOIN u.company c " +
                "WHERE c.name = :companyName " +
                "ORDER BY u.personalInfo.firstname, p.amount", Payment.class)
                .setParameter("companyName", companyName)
                .list();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return session.createQuery(
                "SELECT AVG(p.amount) " +
                "FROM Payment p " +
                "JOIN p.receiver u " +
                "WHERE u.personalInfo.firstname = :firstName " +
                "AND u.personalInfo.lastname = :lastName", Double.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session.createQuery(
                "SELECT c.name, AVG(p.amount) " +
                "FROM Company c " +
                "JOIN c.users u " +
                "JOIN u.payments p " +
                "GROUP BY c.name " +
                "ORDER BY c.name", Object[].class)
                .list();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Object[]> isItPossible(Session session) {
        return session.createQuery(
                "SELECT u, AVG(p.amount) " +
                "FROM User u " +
                "JOIN u.payments p " +
                "GROUP BY u " +
                "HAVING AVG(p.amount) > (SELECT AVG(p.amount) FROM Payment p) " +
                "ORDER BY u.personalInfo.firstname", Object[].class)
                .list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}