package com.iwor.mapper;

import com.iwor.dto.UserReadDto;
import com.iwor.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserReadMapper implements Mapper<User, UserReadDto> {

    private final CompanyReadMapper companyReadMapper;

    @Override
    public UserReadDto mapFrom(User object) {
        return new UserReadDto(
                object.getId(),
                object.getUsername(),
                object.getPersonalInfo(),
                object.getInfo(),
                object.getRole(),
                Optional.ofNullable(object.getCompany())
                        .map(companyReadMapper::mapFrom)
                        .orElse(null)
        );
    }
}
