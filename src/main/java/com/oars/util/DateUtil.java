package com.oars.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {
    private static final String DATE_FORMAT = "YYYY-MM-DD";
    private static final String TIME_FORMAT = "hh:mm:ss";

    public static Date parseDate(String dateStr) throws RuntimeException {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + dateStr);
            throw new RuntimeException(e);
        }
    }

    public static Time parseTime(String timeStr) throws RuntimeException {
        DateFormat format = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        try {
            Date date = format.parse(timeStr);
            return new Time(date.getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + timeStr);
            throw new RuntimeException(e);
        }
    }
}
