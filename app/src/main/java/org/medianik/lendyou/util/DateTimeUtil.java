package org.medianik.lendyou.util;

import androidx.annotation.NonNull;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;

import org.jetbrains.annotations.Nullable;
import org.medianik.lendyou.R;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final Clock clock = Clock.systemUTC();
    private static final int SECONDS_PER_DAY = 60 * 60 * 24;
    public static long daysSinceEpoch(){
        return clock.millis()/1000 / SECONDS_PER_DAY;
    }
    public static boolean isToday(@NonNull LocalDateTime date){
        return isToday(date.toLocalDate());
    }
    public static boolean isToday(@NonNull LocalDate date) {
        return date.toEpochDay() == DateTimeUtil.daysSinceEpoch();
    }
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("hh:mm");
}
