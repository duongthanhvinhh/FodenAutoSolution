package org.foden.constants;

public final class FrameworkConstants {
    private static final String RESOURCE_PATH = System.getProperty("user.dir") + "/src/test/resources";
    private static final String CONFIG_FILE_PATH = RESOURCE_PATH + "/config/config.properties";
    private static final int WAIT_SLEEP_STEP = 0;
    private static final int WAIT_PAGE_LOADED = 20;
    private static final boolean ACTIVE_PAGE_LOADED = true;
    private static final int EXPLICITWAIT = 10;
    private static final int IMPLICTLY_WAIT_TIMEOUT = 50;
    public static String getConfigFilePath(){
        return CONFIG_FILE_PATH;
    }
    public static int getWaitSleepStep(){
        return WAIT_SLEEP_STEP;
    }
    public static int getWaitpageloaded(){
        return WAIT_PAGE_LOADED;
    }
    public static boolean isActivepageloaded(){
        return ACTIVE_PAGE_LOADED;
    }
    public static int getExplicitwait(){
        return EXPLICITWAIT;
    }
    public static int getImplicitwaitTimeout(){
        return IMPLICTLY_WAIT_TIMEOUT;
    }

}
