package com.iwor.entity;

import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Value
public class Birthday implements Comparable<Birthday>, Serializable {

    @Serial
    private static final long serialVersionUID = -8618569372588687192L;

    LocalDate birthDate;

    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    @Override
    public int compareTo(Birthday o) {
        return birthDate.compareTo(o.birthDate);
    }
}
