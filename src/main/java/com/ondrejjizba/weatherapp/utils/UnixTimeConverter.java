package com.ondrejjizba.weatherapp.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UnixTimeConverter {
    public static String converterTime(long unixTime, long timezone) {
        ZonedDateTime zonedDateTime = convertToLocalDateTime(unixTime, timezone);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String converterDayTime(long unixTime, long timezone) {
        ZonedDateTime zonedDateTime = convertToLocalDateTime(unixTime, timezone);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    private static ZonedDateTime convertToLocalDateTime(long unixTime, long timezone) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        ZonedDateTime zonedDateTime;
        if (timezone < 0) {
            zonedDateTime = instant.atZone(ZoneId.of("UTC" + timezone / 3600));
        } else {
            zonedDateTime = instant.atZone(ZoneId.of("UTC+" + timezone / 3600));
        }
        return zonedDateTime;
    }
}
