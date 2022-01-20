package org.medianik.lendyou.util;

import androidx.annotation.NonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Util methods, used for getting days
 */
public class DateTimeUtil {
    private static final Clock clock = Clock.systemUTC();
    private static final int MILLIS_PER_DAY = 60 * 60 * 24 * 1000;

    /**
     * Exact number of days since epoch(1970-01-01) rounded down.
     * <p>
     * Example: if it was 1970-02-01 12:13 PM, then this method returns 1.
     * </p>
     *
     * @return number of days
     */
    public static long daysSinceEpoch() {
        return clock.millis() / MILLIS_PER_DAY;
    }

    /**
     * Determines if date is today, thus if date's number of days is equal to {@code daysSinceEpoch()}'s
     */
    public static boolean isToday(@NonNull LocalDateTime date) {
        return isToday(date.toLocalDate());
    }

    /**
     * Determines if date is today, thus if date's number of days is equal to {@code daysSinceEpoch()}'s
     */
    public static boolean isToday(@NonNull LocalDate date) {
        return date.toEpochDay() == daysSinceEpoch();
    }

    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("hh:mm");

    /**
     * Determines if epochDays is tomorrow or more.
     *
     * @param epochDays Days since epoch
     */
    public static boolean isLaterThanToday(long epochDays) {
        return daysSinceEpoch() > epochDays;
    }

    /**
     * Determines if epochDays is yesterday or less.
     *
     * @param epochDays Days since epoch
     */
    public static boolean isEarlierThanToday(long epochDays) {
        return daysSinceEpoch() < epochDays;
    }
}
