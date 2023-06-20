package com.iwor.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateFormatter {
    private LocalDateFormatter() {
    }

    public static LocalDate format(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
