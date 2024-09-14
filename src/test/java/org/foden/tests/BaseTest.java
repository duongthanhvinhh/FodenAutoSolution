package org.foden.tests;

import io.qameta.allure.Story;
import org.foden.driver.Driver;
import org.foden.utils.DateTimeUtils;
import org.foden.utils.FileDirectoryUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;

public class BaseTest {

    protected BaseTest(){}

    protected SoftAssert softAssert = new SoftAssert();
    long startTime;
    private String story;
    @BeforeClass
    public void cleanFolder() {
        String allureDirPath = System.getProperty("user.dir") + "\\allure-results";
        String allurePrioRunDirPath = System.getProperty("user.dir") + "\\allure-results-prior-run";

        FileDirectoryUtils.cleanDirectory(allurePrioRunDirPath);
        FileDirectoryUtils.copyDirectory(allureDirPath, allurePrioRunDirPath);
        System.out.println("Prior Allure results backed up");
        FileDirectoryUtils.cleanDirectory(allureDirPath);
        System.out.println("Allure folder cleaned");
    }

    @BeforeMethod(alwaysRun = true)
    public synchronized void beforeMethod(Method method){
        softAssert = new SoftAssert();
        startTime = System.currentTimeMillis();
        Story storyAnnotation = method.getAnnotation(Story.class);
        if (storyAnnotation != null){
            story = storyAnnotation.value();
            System.out.println("Starting Story: " + story + " with thread - " + Thread.currentThread().getId());
        }
    }

    @BeforeMethod
    protected synchronized void setUp(){
        Driver.initDriver();
    }

    @AfterMethod
    protected synchronized void tearDown(){
        Driver.quitDriver();
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
