package org.foden.driver;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.foden.enums.ConfigProperties;
import org.foden.utils.DateTimeUtils;
import org.foden.utils.FileDirectoryUtils;
import org.foden.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseSteps extends WebDriverProvider {

    protected BaseSteps(){}
    protected ScheduledExecutorService browserWaker;
    protected SoftAssert softAssert = new SoftAssert();
    long startTime;
    private String story;
    String osName = System.getProperty("os.name");

    public static boolean hasQuit() {
        try {
            getDriver().getTitle();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Initializing WebDriver")
    public void initializeWebDriver() {
        int count=0;
        driver = DriverFactory.webDriverHashmap.get(Thread.currentThread().getId()).get();
        System.out.println("Thread "+Thread.currentThread().getId()+": Driver while intitalizing "+ driver);
        while (hasQuit() == true && count < 3) {
            DriverFactory.getInstance().threadDriver.remove();
            driver = DriverFactory.getInstance().getDriver();
            System.out.println("Retrying driver for " + count + " time(s) and driver value is " + driver);
            count++;
        }
            System.out.println("Driver===============================>>> " + driver);
            initialize(PropertyUtils.get(ConfigProperties.URL), osName);
    }


    @BeforeClass
    public void cleanFolder() {
        String allureDirPath;
        String allurePrioRunDirPath;
        String osName = System.getProperty("os.name");
        if (osName.equalsIgnoreCase("linux")){
            allureDirPath = System.getProperty("user.dir") + "/allure-results";
            allurePrioRunDirPath = System.getProperty("user.dir") + "/allure-results-prior-run";
        } else {
            allureDirPath = System.getProperty("user.dir") + "\\allure-results";
            allurePrioRunDirPath = System.getProperty("user.dir") + "\\allure-results-prior-run";
        }


        FileDirectoryUtils.cleanDirectory(allurePrioRunDirPath);
        FileDirectoryUtils.copyDirectory(allureDirPath, allurePrioRunDirPath);
        System.out.println("Prior Allure results backed up");
        FileDirectoryUtils.cleanDirectory(allureDirPath);
        System.out.println("Allure folder cleaned");
    }

    @BeforeMethod(alwaysRun = true)
    public synchronized void setUp(Method method){
        softAssert = new SoftAssert();
        startTime = System.currentTimeMillis();

        driver = DriverFactory.getInstance().getDriver();

        Story storyAnnotation = method.getAnnotation(Story.class);
        if (storyAnnotation != null){
            story = storyAnnotation.value();
            System.out.println("Starting Story: " + story + " with thread - " + Thread.currentThread().getId());
        }
        runBrowserWaker(Thread.currentThread().getId(), this.driver);
    }

    public void runBrowserWaker(long threadId, WebDriver driverToWake){
        System.out.println("BrowserWaker: Thread " + threadId + " setting up browser waker with driver " + driverToWake);
        browserWaker = Executors.newSingleThreadScheduledExecutor();
        browserWaker.scheduleAtFixedRate(() -> {
            System.out.println("BrowserWaker: Thread " + threadId + " Browser " + driverToWake + " is being awaken");
            try {
                if (driverToWake != null) {
                    //wake session
                    driverToWake.getTitle();
                    System.out.println("BrowserWaker: Thread " + threadId + " Browser " + driverToWake + " is awaken");
                } else {
                    System.out.println("BrowserWaker: Thread " + threadId + " Browser in thread is NULL");
                }
            }
			catch (Throwable t) {
                    System.out.println("BrowserWaker: Thread "+  threadId + " failed waking with driver "+ driverToWake +": " + t.getMessage());
            }
        }, 180, 180, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void tearDown(){
        try {
            System.out.println("Closing browser in tearDown After Method");
            DriverFactory.getInstance().removeDriver();
        } catch (Exception e){
            System.out.println("exit");
            Allure.addAttachment("Close Browser in tearDown FAILED (Potentially crashed)", e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult iTestResult, Method method){
        Story storyAnnotation = method.getAnnotation(Story.class);
        if (storyAnnotation != null){
            story = storyAnnotation.value();
            System.out.println("================================================");
            System.out.println("     Completed @Story: " + story);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            String timeInMinsAndSecs = DateTimeUtils.convertMillisecondsToMinutesSeconds(executionTime);
            System.out.println("     Execution time: " + executionTime + "ms = " + timeInMinsAndSecs);
            if (executionTime > 600000){
                System.out.println("     ALERT SLOWER STORY: " + story);
            }
            String description = iTestResult.getMethod().getDescription();
            if (iTestResult.getStatus() == ITestResult.SUCCESS){
                System.out.println("     @Test PASSED - " + description);
            } else if (iTestResult.getStatus() == ITestResult.FAILURE){
                System.out.println("     @Test FAILED - " + description);
                System.out.println("Details: " + iTestResult.getThrowable());
            } else {
                System.out.println("     @Test " + iTestResult.getStatus() + " - " + description);
            }
            System.out.println("================================================");
        }
    }
}
