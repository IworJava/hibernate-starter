package com.iwor;

import com.iwor.entity.User;
import com.iwor.util.ConnectionManager;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = User.builder()
                .username("ivan@gmail.com")
                .firstname("Ivan")
                .lastname("Ivanov")
//                .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                .build();

        String sql = """
                INSERT INTO %s
                (%s)
                VALUES
                (%s)
                """;

        String tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getSimpleName().toLowerCase());

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

        String sqlQuery = sql.formatted(tableName, columnNames, values);
        System.out.println(sqlQuery);
//        declaredFields = Arrays.stream(declaredFields)
//                .sorted(Comparator.comparing(Field::getName))
//                .toArray(Field[]::new);

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                field.setAccessible(true);
                statement.setObject(i + 1, declaredFields[i].get(user));
            }
            statement.execute();
        }
    }
}