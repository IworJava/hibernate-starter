package com.iwor.dto;

import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Role;

public record UserCreateDto(String username,
                            PersonalInfo personalInfo,
                            String info,
                            Role role,
                            Integer companyId) {
}
