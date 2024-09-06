package org.foden.driver;

import com.google.common.util.concurrent.Uninterruptibles;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.foden.enums.ConfigProperties;
import org.foden.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DriverFactory {

    private DriverFactory(){}

    private static final String osName = System.getProperty("os.name");

    public static WebDriver getDriver(String browser) throws MalformedURLException {

            switch (browser.toLowerCase()){
                case "edge":
                case "chrome":
                    if (osName != null && osName.equalsIgnoreCase("linux")){
                        try{
                            String chromeDriverURL = PropertyUtils.get(ConfigProperties.GRIDURL);
                            URL chromeUrl = new URL(chromeDriverURL);
                            System.out.println("chromeUrl----" + chromeUrl);
                            return new RemoteWebDriver(chromeUrl, setChromeCapability());
                        } catch (Exception e){
                            System.out.println("Exception occurs while creating driver instance " + e);
                            return null;
                        }
                    } else if (osName != null && osName.contains("Windows")) {
                        System.out.println("Chrome running on Local/Windows OS");
                        return new ChromeDriver(setChromeCapability());
                    }
                    break;

                case "firefox":
                default: return new ChromeDriver(setChromeCapability());
            }
        return null;
    }

    private static ChromeOptions setChromeCapability(){
        ChromeOptions options = new ChromeOptions();
        HashMap<String,Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups",0);
        chromePrefs.put("download.prompt_for_download", "false");
        chromePrefs.put("download.default_directory",System.getProperty("user.dir")+ File.separatorChar+"downloaded");
        options.addArguments("--disable-notifications");

        if(osName!=null && osName.equalsIgnoreCase("linux")){
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--no-sandbox");
            options.addArguments("start-maximized");
            options.addArguments("disable-infobars");
            options.addArguments("--disable-gpu");
            options.addArguments("--headless");
            String user_agent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36";
            options.addArguments("--user-agent={" + user_agent + "}");
        } else if (osName != null && osName.contains("Windows")) {
            options.addArguments("--remote-allow-origins=*");
        }
        options.setExperimentalOption("prefs", chromePrefs);
        ChromeOptions cap = new ChromeOptions();
        cap.setAcceptInsecureCerts(true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        return cap;
    }
}
