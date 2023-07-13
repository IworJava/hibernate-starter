package com.iwor.mapper;

import com.iwor.dao.CompanyRepository;
import com.iwor.dto.UserCreateDto;
import com.iwor.entity.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {

    private final CompanyRepository companyRepository;

    @Override
    public User mapFrom(UserCreateDto object) {
        return User.builder()
                .username(object.username())
                .personalInfo(object.personalInfo())
                .info(object.info())
                .role(object.role())
                .company(companyRepository.findById(object.companyId())
                        .orElseThrow(IllegalArgumentException::new))
                .build();
    }
}
