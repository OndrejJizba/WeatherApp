package com.ondrejjizba.weatherapp.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UnixTimeConverter {
    public static String converter(long unixTime, long timezone) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC+" + timezone/3600));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
