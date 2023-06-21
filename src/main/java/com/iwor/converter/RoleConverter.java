package com.iwor.converter;

import com.iwor.entity.Role;

import javax.persistence.AttributeConverter;

public class RoleConverter implements AttributeConverter<Role, String> {
    private static final String ROLE_ADMIN = "A";
    private static final String ROLE_USER = "U";

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return switch (attribute) {
            case ADMIN -> ROLE_ADMIN;
            case USER -> ROLE_USER;
        };
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return ROLE_ADMIN.equals(dbData)
                ? Role.ADMIN
                : Role.USER;
    }
}
