package org.foden.utils;

import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String convertMillisecondsToMinutesSeconds(long milliseconds) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02dm:%02ds", minutes, seconds);
    }
}
