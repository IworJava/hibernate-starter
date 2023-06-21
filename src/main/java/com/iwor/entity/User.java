package com.iwor.entity;

import com.iwor.converter.BirthdayConverter;
import com.iwor.converter.RoleConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    private String username;
    private String firstname;
    private String lastname;
    @Column(name = "birth_date")
    @Convert(converter = BirthdayConverter.class)
    private Birthday birthDate;
    @Convert(converter = RoleConverter.class)
    private Role role;
}
