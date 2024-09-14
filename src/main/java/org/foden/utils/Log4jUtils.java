package org.foden.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jUtils {
    private static final Logger Log =  LogManager.getLogger(Log4jUtils.class);
    public static void info (String message) {
        Log.info(message);
    }
    public static void info (Object object) {
        Log.info(object);
    }
    public static void warn (String message) {
        Log.warn(message);
    }
    public static void warn (Object object) {
        Log.warn(object);
    }

    public static void error (String message) {
        Log.error(message);
    }

    public static void error (Object object) {
        Log.error(object);
    }
    public static void fatal (String message) {
        Log.fatal(message);
    }
    public static void debug (String message) {
        Log.debug(message);
    }
    public static void debug (Object object) {
        Log.debug(object);
    }
}
