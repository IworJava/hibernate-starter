package com.iwor.dto;

import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Role;

//@Builder
public record UserReadDto(Long id,
                          String username,
                          PersonalInfo personalInfo,
                          String info,
                          Role role,
                          CompanyReadDto company) {
}
