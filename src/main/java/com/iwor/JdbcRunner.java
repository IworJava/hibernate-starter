package com.iwor;

import com.iwor.converter.RoleConverter;
import com.iwor.entity.Birthday;
import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Programmer;
import com.iwor.entity.User;
import com.iwor.util.ConnectionManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    private static final String FIND_ALL_SQL = "SELECT * FROM users";

    public static void main(String[] args) throws SQLException {
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
        PersonalInfo personalInfo = PersonalInfo.builder()
                .firstname(resultSet.getObject("firstname", String.class))
                .lastname(resultSet.getObject("lastname", String.class))
                .birthDate(new Birthday(resultSet.getObject("birth_date", Date.class).toLocalDate()))
                .build();
        return Programmer.builder()
                .username(resultSet.getObject("username", String.class))
                .personalInfo(personalInfo)
                .role(new RoleConverter().convertToEntityAttribute(resultSet.getString("role")))
                .build();
    }
}
