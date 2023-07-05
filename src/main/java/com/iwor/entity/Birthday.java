package com.iwor.entity;

import lombok.Value;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Value
public class Birthday implements Comparable<Birthday> {

    LocalDate birthDate;

    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    @Override
    public int compareTo(Birthday o) {
        return birthDate.compareTo(o.birthDate);
    }
}
