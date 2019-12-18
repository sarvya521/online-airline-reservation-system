package com.oars.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";

    public static LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalTime parseTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH);
        return LocalTime.parse(timeStr, formatter);
    }

    public static int lastDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static void main(String[] args) {
        String dateStr = "2019-12-24";
        System.out.println(parseDate(dateStr));
        String timeStr = "16:10";
        System.out.println(parseTime(timeStr));
        System.out.println(YearMonth.of(2019, 8).lengthOfMonth());
    }
}
