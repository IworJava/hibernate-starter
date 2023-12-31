package com.iwor;

import com.iwor.converter.BirthdayConverter;
import com.iwor.converter.RoleConverter;
import com.iwor.entity.Birthday;
import com.iwor.entity.Programmer;
import com.iwor.entity.Role;
import com.iwor.entity.User;
import com.iwor.util.ConnectionManager;
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
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernateReflectionTest {
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
    void checkInsertReflectionApi() throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Programmer user = Programmer.builder()
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
        Programmer expectedResult = Programmer.builder()
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
