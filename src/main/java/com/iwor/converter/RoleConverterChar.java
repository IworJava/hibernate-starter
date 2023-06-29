package com.iwor.converter;

import com.iwor.entity.Role;

import javax.persistence.AttributeConverter;
import java.util.Optional;

public class RoleConverterChar implements AttributeConverter<Role, Character> {
    private static final Character ROLE_ADMIN = 'A';
    private static final Character ROLE_USER = 'U';

    @Override
    public Character convertToDatabaseColumn(Role attribute) {
        return Optional.ofNullable(attribute)
                .map(role -> switch (attribute) {
                    case ADMIN -> ROLE_ADMIN;
                    case USER -> ROLE_USER;
                }).orElse(null);
    }

    @Override
    public Role convertToEntityAttribute(Character dbData) {
        return Optional.ofNullable(dbData)
                .map(data -> ROLE_ADMIN.equals(dbData)
                        ? Role.ADMIN
                        : Role.USER)
                .orElse(null);
    }
}
