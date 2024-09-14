package org.foden.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {
        super();
    }

    public static String getCurrentDate() {
        Date date = new Date();
        return date.toString().replace(":", "_").replace(" ", "_");
    }

    public static String getCurrentDateTime() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(now);
    }

    public static String getCurrentDateTimeCustom(String separator_Character) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = formatter.format(now).replace("/", separator_Character);
        timeStamp = timeStamp.replace(" ", separator_Character);
        timeStamp = timeStamp.replace(":", separator_Character);
        return timeStamp;
    }
}
