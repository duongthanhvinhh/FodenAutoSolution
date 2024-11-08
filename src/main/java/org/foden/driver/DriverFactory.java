package org.foden.driver;

import io.qameta.allure.Allure;
import org.foden.enums.ConfigProperties;
import org.foden.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class DriverFactory {

    private DriverFactory(){}
    private static final String osName = System.getProperty("os.name");

    public static ConcurrentHashMap<Long, ThreadLocal<WebDriver>> webDriverHashmap = new ConcurrentHashMap<>();

    private WebDriver driver;
    private static DriverFactory instance = new DriverFactory();

    public static synchronized DriverFactory getInstance() {
        if (instance == null) {
            instance = new DriverFactory();
        }
        return instance;
    }

    ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>(){
        @Override
        protected WebDriver initialValue(){
            switch (PropertyUtils.get(ConfigProperties.BROWSER).toLowerCase()){
                    case "edge":
                    case "chrome":
                        if (Objects.equals(PropertyUtils.get(ConfigProperties.USESELENIUMGRID), "yes") && (osName != null && osName.equalsIgnoreCase("linux"))){
                            try{
                                String chromeDriverURL = PropertyUtils.get(ConfigProperties.GRIDURL);
                                URL chromeUrl = new URL(chromeDriverURL);
                                System.out.println("chromeUrl----" + chromeUrl);
                                return new RemoteWebDriver(chromeUrl, setChromeCapability(PropertyUtils.get(ConfigProperties.HEADLESS)));
                            } catch (Exception e){
                                System.out.println("Exception occurs while creating driver instance " + e);
                                return null;
                            }
                        } else if ((osName != null && osName.contains("Windows")) || (osName != null && osName.contains("Linux"))) {
                            System.out.println("Chrome running on Local/Windows OS");
                            return new ChromeDriver(setChromeCapability(PropertyUtils.get(ConfigProperties.HEADLESS)));
                        }
                        break;

                    case "firefox":
                        if (Objects.equals(PropertyUtils.get(ConfigProperties.USESELENIUMGRID), "yes") && (osName != null && osName.equalsIgnoreCase("linux"))){
                            try {
                                String firefoxDriverURL = PropertyUtils.get(ConfigProperties.GRIDURL);
                                URL firefoxUrl = new URL(firefoxDriverURL);
                                System.out.println("firefoxUrl----" + firefoxUrl);
                                return new RemoteWebDriver(firefoxUrl, setFirefoxCapability(PropertyUtils.get(ConfigProperties.HEADLESS)));
                            } catch (Exception e) {
                                System.out.println("Exception occurs while creating Firefox driver instance " + e);
                                return null;
                            }
                        } else if ((osName != null && osName.contains("Windows")) || (osName != null && osName.contains("Linux"))) {
                            System.out.println("Firefox running on Local/Windows OS");
                            return new FirefoxDriver(setFirefoxCapability(PropertyUtils.get(ConfigProperties.HEADLESS)));
                        }
                        break;
                    default: return new ChromeDriver(setChromeCapability(PropertyUtils.get(ConfigProperties.HEADLESS)));
            }
            return null;
        }
    };

    public WebDriver getDriver(){
        webDriverHashmap.put(Thread.currentThread().getId(), threadDriver);
        System.out.println("Drivers in webDriver Hashmaps are " + webDriverHashmap.get(Thread.currentThread().getId()));
        driver = webDriverHashmap.get(Thread.currentThread().getId()).get();
        return driver;
    }

    public void removeDriver(){
        driver = webDriverHashmap.get(Thread.currentThread().getId()).get();
        if (Objects.isNull(driver)){
            return;
        }
        Allure.addAttachment("Quitting Driver in RemoveDriver ", "");
        System.out.println("Thread " + Thread.currentThread().getId() + " quitting session " + driver);
        driver.quit();
    }

    public void removeDriverFromThreadLocal(){
        threadDriver.remove();
    }

    private static ChromeOptions setChromeCapability(String headless) {
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.prompt_for_download", "false");
        chromePrefs.put("download.default_directory", System.getProperty("user.dir") + File.separatorChar + "downloaded");
        options.setExperimentalOption("prefs", chromePrefs);

        options.addArguments("--disable-notifications");

        if (osName != null && osName.equalsIgnoreCase("linux")) {
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--no-sandbox");
            options.addArguments("start-maximized");
            options.addArguments("disable-infobars");
            options.addArguments("--disable-gpu");
            String user_agent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36";
            options.addArguments("--user-agent=" + user_agent);
        } else if (osName != null && osName.contains("Windows")) {
            options.addArguments("--remote-allow-origins=*");
        }

        if ("yes".equalsIgnoreCase(headless)) {
            options.addArguments("--headless=new");
        }

        options.setAcceptInsecureCerts(true);

        return options;
    }

    private static FirefoxOptions setFirefoxCapability(String headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless.equals("yes")) {
            options.addArguments("--headless");
        }
        options.setAcceptInsecureCerts(true);
        // Add more Firefox configurations as necessary
        return options;
    }
}

