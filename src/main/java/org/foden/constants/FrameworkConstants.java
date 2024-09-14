package org.foden.constants;

public final class FrameworkConstants {
    private static final String RESOURCEPATH = System.getProperty("user.dir") + "/src/test/resources";
    private static final String CONFIGFILEPATH = RESOURCEPATH + "/config/config.properties";
    private static final int WAITSLEEPSTEP = 0;
    private static final int WAITPAGELOADED = 20;
    private static final boolean ACTIVEPAGELOADED = true;
    private static final int EXPLICITWAIT = 10;
    public static String getConfigFilePath(){
        return CONFIGFILEPATH;
    }
    public static int getWaitsleepstep(){
        return WAITSLEEPSTEP;
    }
    public static int getWaitpageloaded(){
        return WAITPAGELOADED;
    }
    public static boolean isActivepageloaded(){
        return ACTIVEPAGELOADED;
    }
    public static int getExplicitwait(){
        return EXPLICITWAIT;
    }

}
