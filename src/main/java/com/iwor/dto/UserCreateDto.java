package com.iwor.dto;

import com.iwor.entity.PersonalInfo;
import com.iwor.entity.Role;
import com.iwor.validation.UpdateCheck;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record UserCreateDto(@NotNull
                            String username,
                            @Valid
                            PersonalInfo personalInfo,
                            String info,
                            @NotNull(groups = UpdateCheck.class)
                            Role role,
                            Integer companyId) {
}
