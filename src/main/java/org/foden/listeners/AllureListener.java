package org.foden.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.foden.driver.WebDriverProvider;
import org.foden.utils.Log4jUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AllureListener implements ITestListener, ISuiteListener {

    private static String getTestMethodName(ITestResult iTestResult){
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

//    @Attachment(value = "Screenshot", type = "image/png", fileExtension = ".png")
//    public byte[] saveScreenshotPNG() {
//        return ((TakesScreenshot) WebDriverProvider.getDriver()).getScreenshotAs(OutputType.BYTES);
//    }

    @Attachment(value = "screenshot", type = "image/png", fileExtension = ".png")
    public byte[] saveScreenshotPNG() {
        return ((TakesScreenshot) Objects.requireNonNull(WebDriverProvider.getDriver())).getScreenshotAs(OutputType.BYTES);
    }

    public static File takeScreenshot(WebDriver driver, String testName){
        Log4jUtils.info("Capturing the screenshot :: takeScreenshot");
        String screenShotPath = null;
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File src = takesScreenshot.getScreenshotAs(OutputType.FILE);
        try {
            screenShotPath = System.getProperty("user.dir") + "\\target" + "\\allure-results" + testName + "_screenshot.png";
            Log4jUtils.info("The screenshot is saved at " + screenShotPath);
            FileUtils.copyFile(src, new File(screenShotPath));
        } catch (IOException e) {
            Log4jUtils.error("Failed to capture the screenshot :: takeScreenshot " + e);
        }
        return new File(screenShotPath);
    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message){
        return message;
    }

    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html){
        return html;
    }

    @Override
    public void onStart(ITestContext iTestContext){
        Log4jUtils.info("I am in onStart method " + iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext){
        Log4jUtils.info("I am in onFinish method " + iTestContext.getName());
    }

    @Override
    public void onTestStart(ITestResult iTestResult){
        Log4jUtils.info(getTestMethodName(iTestResult) + " test is starting.");
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult){
        Log4jUtils.info(getTestMethodName(iTestResult) + " test is succeed.");

    }

    @Override
    public void onTestFailure(ITestResult iTestResult){

        Log4jUtils.error(getTestMethodName(iTestResult) + " test is failed.");

        if (WebDriverProvider.getDriver() != null){
            System.out.println("Screenshot captured for test case: " + getTestMethodName(iTestResult));

            byte[] screenshot = saveScreenshotPNG();
            if (screenshot!=null){
                Log4jUtils.info("Screenshot saved successfully.");
            } else {
                Log4jUtils.warn("Failed to save screenshot.");
            }

        } else Log4jUtils.warn("Driver is null, can not take screenshot");

        saveTextLog(getTestMethodName(iTestResult) + " failed and screenshot taken.");

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        Log4jUtils.info(getTestMethodName(iTestResult) + " test is skipped.");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        Log4jUtils.info("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
    }
}
