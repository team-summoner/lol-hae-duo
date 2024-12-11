package com.summoner.lolhaeduo.common.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Component
public class TimeUtil {

    public static long startTimeInEpoch(int days) {
        Instant now = Instant.now();
        Instant past = now.minus(days, ChronoUnit.DAYS);
        return past.getEpochSecond();
    }

    public static long convertToEpochSeconds(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime is null");
        }
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
