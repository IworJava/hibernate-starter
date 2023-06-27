package com.iwor;

import com.iwor.converter.BirthdayConverter;
import com.iwor.converter.RoleConverter;
import com.iwor.entity.Birthday;
import com.iwor.entity.Chat;
import com.iwor.entity.Company;
import com.iwor.entity.LocaleInfo;
import com.iwor.entity.Profile;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.entity.UserChat;
import com.iwor.util.ConnectionManager;
import com.iwor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {
    private static final String INSERT_SQL = """
            INSERT INTO %s
            (%s)
            VALUES
            (%s)
            """;
    private static final String GET_BY_ID_SQL = """
            SELECT *
            FROM %s
            WHERE %s = ?
            """;

    private final RoleConverter roleConverter = new RoleConverter();
    private final BirthdayConverter birthdayConverter = new BirthdayConverter();

    @Test
    void checkLocaleInfo() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Company company = session.get(Company.class, 3);
            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
            company.getLocales().add(LocaleInfo.of("en", "English description"));

            transaction.commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, 3L);
            Chat chat = session.get(Chat.class, 4L);
            UserChat userChat = UserChat.builder()
                    .user(user)
                    .chat(chat)
                    .createdAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();
            session.save(userChat);

            transaction.commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Profile profile = Profile.builder()
                    .street("Arbat, 10")
                    .language("ru")
                    .build();
            User user = User.builder()
                    .username("test1@gmail.com")
                    .profile(profile)
                    .build();
            session.save(user);

            transaction.commit();
        }
    }

    @Test
    void checkInsertReflectionApi() throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        User user = User.builder()
                .username("ivan@gmail.com")
//                .firstname("Ivan")
//                .lastname("Ivanov")
//                .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                .role(Role.USER)
                .build();

        String tableName = getTableName(user.getClass());

        Field[] declaredFields = user.getClass().getDeclaredFields();

        String columnNames = Arrays.stream(declaredFields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()).toLowerCase())
//                .sorted()
                .collect(Collectors.joining(", "));

        String values = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        String sqlQuery = INSERT_SQL.formatted(tableName, columnNames, values);
        System.out.println(sqlQuery);
//        declaredFields = Arrays.stream(declaredFields)
//                .sorted(Comparator.comparing(Field::getName))
//                .toArray(Field[]::new);

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                field.setAccessible(true);

                statement.setObject(i + 1, getDBValue(field, user));
            }
            statement.execute();
        }
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        User expectedResult = User.builder()
                .username("ivan@gmail.com")
//                .firstname("Ivan")
//                .lastname("Ivanov")
//                .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                .role(Role.USER)
                .build();
        Class<? extends User> clazz = expectedResult.getClass();
        Field[] fields = clazz.getDeclaredFields();

        String idFieldName = null;
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                idFieldName = field.getName();
            }
        }
        String query = GET_BY_ID_SQL.formatted(getTableName(clazz), idFieldName);

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, expectedResult.getUsername());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new NoSuchElementException("No such username");
            }

            User actualResult = clazz.getConstructor().newInstance();

            for (Field field : fields) {
                field.setAccessible(true);
                field.set(actualResult, getEntityFieldValue(resultSet, field));
            }

            Assertions.assertEquals(expectedResult, actualResult);
            System.out.println(actualResult);
        }
    }

    private Object getDBValue(Field field, Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = field.get(entity);
        if (field.getAnnotation(Convert.class) == null) {
            return value;
        }
        Class converterClazz = field.getAnnotation(Convert.class).converter();
        if (converterClazz.equals(RoleConverter.class)) {
            return converterClazz.getDeclaredMethod("convertToDatabaseColumn", Role.class)
                    .invoke(roleConverter, value);
        }
        if (converterClazz.equals(BirthdayConverter.class)) {
            return converterClazz.getDeclaredMethod("convertToDatabaseColumn", Birthday.class)
                    .invoke(birthdayConverter, value);
        }
        return null;
    }

    private Object getEntityFieldValue(ResultSet rs, Field field) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String columnName = getColumnName(field);
        Object value = rs.getObject(columnName);

        Type typeAnnotation = field.getAnnotation(Type.class);
        if (typeAnnotation != null) {
            return null;
        }
        Convert convertAnnotation = field.getAnnotation(Convert.class);
        if (convertAnnotation == null) {
            return value;
        }
        Class converter = convertAnnotation.converter();
        if (converter.equals(RoleConverter.class)) {
            return converter.getDeclaredMethod("convertToEntityAttribute", String.class)
                    .invoke(roleConverter, value);
        }
        if (converter.equals(BirthdayConverter.class)) {
            return converter.getDeclaredMethod("convertToEntityAttribute", Date.class)
                    .invoke(birthdayConverter, value);
        }
        return null;
    }

    private String getColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::name)
                .orElse(field.getName());
    }

    private String getTableName(Class<? extends User> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Table.class))
                .map(a -> (
                        a.schema().isBlank()
                                ? "public"
                                : a.schema())
                        + "." + a.name()
                )
                .orElse(clazz.getSimpleName().toLowerCase());
    }
}