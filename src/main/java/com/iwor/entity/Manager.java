package com.iwor.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Entity
@DiscriminatorValue("m")
public class Manager extends User {

    private String projectName;

    public Manager(Long id, String username, PersonalInfo personalInfo, Role role, String info, Company company, Profile profile, List<UserChat> userChats, List<Payment> payments, String projectName) {
        super(id, username, personalInfo, role, info, company, profile, userChats, payments);
        this.projectName = projectName;
    }
}
