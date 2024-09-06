package org.foden.tests;

import org.foden.driver.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected BaseTest(){}

    @BeforeMethod
    protected synchronized void setUp(){
        Driver.initDriver();
    }

    @AfterMethod
    protected synchronized void tearDown(){
        Driver.quitDriver();
    }
}
