package org.foden.driver;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foden.constants.FrameworkConstants;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class WebDriverProvider {

    protected WebDriver driver;


    @Step("Initialize in {1} with URL: {0} ")
    public WebDriver initialize(String url, String osName) {
        driver = DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get();
        if(driver!=null) {
            // Potential cause of Session ID is null. Using WebDriver after calling quit()?
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.getImplicitwaitTimeout()));
            } catch (Exception e){
                //
                Allure.addAttachment("Failed to initialize", "");
            }
            driver.manage().deleteAllCookies();

            Instant start = Instant.now();
            driver.get(url);
            Instant end = Instant.now();
            long seconds = Duration.between(start, end).getSeconds();
            System.out.println("The browser took " + seconds + " seconds to launch.");
            //In Linux box do not resize the window
            if(osName!=null && osName.equalsIgnoreCase("linux")){
                DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get().manage().window().maximize();
                System.out.println("**************maximized****************");
                System.out.println("linux box");
            }else{
                java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) screenSize.getWidth();
                int height = (int) (screenSize.getHeight() * 0.85);

                // Resize the browser window to the full width of the desktop and the current height
                DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get().manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
                System.out.println("************sized the window in web driver provider***********");
                org.openqa.selenium.Point targetPosition = new org.openqa.selenium.Point(0, 0);
                DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get().manage().window().setPosition(targetPosition);
            }
        }
        else
            Assert.fail(":: UNABLE to launch web browser ::");
        return DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get();
    }

    public static WebDriver getDriver() {
        return DriverFactory.webDriverHashmap.get(Thread.currentThread().getId())!=null?DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get():null;
    }

}
