package com.summoner.lolhaeduo.common.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TimeUtil {

    public long startTimeInEpoch(int days) {
        Instant now = Instant.now();
        Instant past = now.minus(days, ChronoUnit.DAYS);
        return past.getEpochSecond();
    }
}
