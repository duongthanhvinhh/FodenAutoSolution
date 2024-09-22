package org.foden.driver;

import org.foden.enums.ConfigProperties;
import org.foden.exceptions.BrowserInvocationFailedException;
import org.foden.utils.PropertyUtils;

import java.net.MalformedURLException;
import java.util.Objects;

public final class Driver {

    private Driver(){}

    public static void initDriver(){
        if (Objects.isNull(DriverManager.getDriver())){
            try {
                DriverManager.setDriver(DriverFactory.getDriver());
            } catch (MalformedURLException e) {
                throw new BrowserInvocationFailedException("Browser invocation failed. Please check the capabilities of browser!");
            }
            DriverManager.getDriver().manage().window().maximize();
            DriverManager.getDriver().get(PropertyUtils.get(ConfigProperties.URL));
        }
    }

    public static void quitDriver(){
        if (Objects.nonNull(DriverManager.getDriver())){
            DriverManager.getDriver().quit();
            DriverManager.unloadDriver();
        }
    }

}
