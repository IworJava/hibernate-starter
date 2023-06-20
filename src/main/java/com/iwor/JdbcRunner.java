package com.iwor;

import com.iwor.entity.User;
import com.iwor.util.ConnectionManager;
import com.iwor.util.LocalDateFormatter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    private static final String FIND_ALL_SQL = "SELECT * FROM users";

    @SneakyThrows
    public static void main(String[] args) {
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
        ) {
            List<User> users = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            System.out.println(users);
        }
    }

    private static User buildUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .username(resultSet.getObject("username", String.class))
                .firstname(resultSet.getObject("firstname" , String.class))
                .lastname(resultSet.getObject("lastname", String.class))
                .birthDate(LocalDateFormatter.format(resultSet.getString("birth_date")))
                .age(resultSet.getObject("age", Integer.class))
                .build();
    }
}
