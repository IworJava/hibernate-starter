package com.iwor.entity;

import com.iwor.converter.BirthdayConverter;
import com.iwor.validation.UpdateCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
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

//    @NotNull
    @NotNull(groups = UpdateCheck.class)
    @Column(name = "birth_date", columnDefinition = "date", nullable = false)
    @Convert(converter = BirthdayConverter.class)
    private Birthday birthDate;
}
