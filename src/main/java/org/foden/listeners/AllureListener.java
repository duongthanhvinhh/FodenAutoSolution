package org.foden.listeners;

import io.qameta.allure.Attachment;
import org.checkerframework.checker.units.qual.A;
import org.foden.driver.DriverManager;
import org.foden.utils.Log4jUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllureListener implements ITestListener{

    private static String getTestMethodName(ITestResult iTestResult){
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(WebDriver driver){
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
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
        Log4jUtils.info("I am in onFinish method" + iTestContext.getName());
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
        Log4jUtils.info(getTestMethodName(iTestResult) + " test is failed.");
        WebDriver driver = DriverManager.getDriver();
        if (driver != null){
            System.out.println("Screenshot captured for test case: " + getTestMethodName(iTestResult));
            saveScreenshotPNG(driver);
        }
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
