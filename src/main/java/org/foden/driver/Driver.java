package org.foden.driver;

import org.foden.exceptions.BrowserInvocationFailedException;

import java.net.MalformedURLException;
import java.util.Objects;

public final class Driver {

    private Driver(){}

    public static void initDriver(){
        if (Objects.isNull(DriverManager.getDriver())){
            try {
                DriverManager.setDriver(DriverFactory.getDriver("chrome")); //TODO: Implement config property file here
            } catch (MalformedURLException e) {
                throw new BrowserInvocationFailedException("Browser invocation failed. Please check the capabilities of browser!");
            }
            DriverManager.getDriver().manage().window().maximize();
            DriverManager.getDriver().get("https://facebook.com"); //TODO: Implement config property file here
        }
    }

    public static void quitDriver(){
        if (Objects.nonNull(DriverManager.getDriver())){
            DriverManager.getDriver().quit();
            DriverManager.unloadDriver();
        }
    }

}
