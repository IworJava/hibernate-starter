package com.iwor.converter;

import com.iwor.entity.Role;

import javax.persistence.AttributeConverter;
import java.util.Optional;

public class RoleConverter implements AttributeConverter<Role, String> {
    private static final String ROLE_ADMIN = "A";
    private static final String ROLE_USER = "U";

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return Optional.ofNullable(attribute)
                .map(role -> switch (attribute) {
                    case ADMIN -> ROLE_ADMIN;
                    case USER -> ROLE_USER;
                }).orElse(null);
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .map(data -> ROLE_ADMIN.equals(dbData)
                        ? Role.ADMIN
                        : Role.USER)
                .orElse(null);
    }
}
