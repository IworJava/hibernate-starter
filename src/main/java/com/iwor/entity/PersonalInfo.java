package com.iwor.entity;

import com.iwor.converter.BirthdayConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PersonalInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8447826393713500223L;

    @Column(length = 128)
    private String firstname;

    @Column(length = 128)
    private String lastname;

    @Column(name = "birth_date", columnDefinition = "date")
    @Convert(converter = BirthdayConverter.class)
    private Birthday birthDate;
}
