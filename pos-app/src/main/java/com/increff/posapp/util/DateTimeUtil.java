package com.increff.posapp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private DateTimeUtil() {}
    public static String getDateTimeString(ZonedDateTime zonedDateTime, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return zonedDateTime.format(formatter);
    }

    public static String getDateTimeString(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    public static ZonedDateTime getZonedDateTime(String zone){
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of(zone);
        return ZonedDateTime.of(localDateTime, zoneId);
    }

}
