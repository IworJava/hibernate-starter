package com.iwor.entity;

import com.iwor.converter.RoleConverter;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "users", schema = "public")
public class User {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(generator = "user_gen", strategy = GenerationType.SEQUENCE)
//    @SequenceGenerator(name = "user_gen", sequenceName = "users_id_seq", allocationSize = 1)
//    private Long id;
    @Column(unique = true)
    private String username;
    @EmbeddedId
//    @AttributeOverride(name = "birthdate", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;
    @Convert(converter = RoleConverter.class)
    private Role role;
    @Type(type = "jsonb")
    private String info;
}
