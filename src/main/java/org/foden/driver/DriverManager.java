package org.foden.driver;

import org.openqa.selenium.WebDriver;

import java.util.Objects;

public final class DriverManager {

    private DriverManager(){}

    private static ThreadLocal<WebDriver> tdriver = new ThreadLocal<>();

    public static WebDriver getDriver(){
        return tdriver.get();
    }

    static void setDriver(WebDriver driver){
        if (Objects.nonNull(driver)){
            tdriver.set(driver);
        }
    }

    public static void unloadDriver(){
        tdriver.remove();
    }
}
