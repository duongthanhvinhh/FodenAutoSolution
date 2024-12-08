package org.foden.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    protected static final List<String> formatters = Arrays.asList(
            "HH:mm X MM/dd/yyyy",
            "hh:mm a X MM/dd/yyyy",
            "HH:mm Z MM/dd/yyyy",
            "hh:mm a Z MM/dd/yyyy",
            "HH:mm X MM/dd/yyyy",
            "HH:mm Z MM/dd/yyyy"
    );

    public static Date parseDate(String dateString) {
        for (String formatString : formatters) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean isDiffTimeAcceptable(LocalDateTime dateTime1, LocalDateTime dateTime2, long diff, TimeUnit timeUnit) {
        Duration duration = Duration.between(dateTime1, dateTime2);
        if (timeUnit.equals(TimeUnit.MINUTES)) {
            long diffInMin = duration.toMinutes();
            if (diffInMin < diff) {
                return true;
            }
        } else if (timeUnit.equals(TimeUnit.SECONDS)) {
            long diffInSec = duration.toSeconds();
            if (diffInSec < diff) {
                return true;
            }
        }
        return false;
    }


    public static String convertMillisecondsToMinutesSeconds(long milliseconds) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02dm:%02ds", minutes, seconds);
    }


}
