package org.foden.tests;

import io.qameta.allure.*;
import org.foden.enums.TestGroups;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({org.foden.listeners.AllureListener.class})
public class LoginTest extends BaseTest{

    private LoginTest(){}

    @Feature("Log in - Log out")
    @Test(priority = 0,description = "First demo testcase", groups = {"FAS-110", "Login", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-110")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the first demo testcase")
    @Owner("Foden Duong")
    public void firstTestcase(){
        Assert.assertTrue(true);
    }
    @Feature("Log in - Log out")
    @Test(priority = 0,description = "Second demo testcase", groups = {"FAS-111", "Login", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-111")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the second demo testcase")
    @Owner("Foden Duong")
    public void secondTestcase(){
        Assert.assertTrue(true);
    }
    @Feature("Log in - Log out")
    @Test(priority = 0,description = "Third demo testcase", groups = {"FAS-112", "Login", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-112")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the third demo testcase")
    @Owner("Foden Duong")
    public void thirdTestcase(){
        Assert.assertTrue(true);
    }

}
