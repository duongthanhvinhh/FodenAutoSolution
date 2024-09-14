package org.foden.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({org.foden.listeners.AllureListener.class})
@Feature("Log in - Log out")
public class LoginTest extends BaseTest{

    private LoginTest(){}

    @Test(priority = 0,description = "First demo testcase")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the first demo testcase")
    @Story("FAS-110")
    public void loginPageUiVerification(){
        Assert.assertTrue(true);
    }
}
