package org.medianik.lendyou.util;

import androidx.annotation.NonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final Clock clock = Clock.systemUTC();
    private static final int MILLIS_PER_DAY = 60 * 60 * 24 * 1000;

    public static long daysSinceEpoch() {
        return clock.millis() / MILLIS_PER_DAY;
    }

    public static boolean isToday(@NonNull LocalDateTime date) {
        return isToday(date.toLocalDate());
    }

    public static boolean isToday(@NonNull LocalDate date) {
        return date.toEpochDay() == daysSinceEpoch();
    }

    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("hh:mm");

    public static boolean isLaterThanToday(@NonNull long epochDays) {
        return daysSinceEpoch() > epochDays;
    }

    public static boolean isEarlierThanToday(@NonNull long epochDays) {
        return daysSinceEpoch() < epochDays;
    }
}
