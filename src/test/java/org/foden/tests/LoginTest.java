package org.foden.tests;

import io.qameta.allure.*;
import org.foden.enums.TestGroups;
import org.foden.pages.LoginPage;
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
        LoginPage.getInstance().goToLoginPage();
        LoginPage.getInstance().login("foden1706@gmail.com", "Password@01");
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
    
    @Feature("Log in - Log out")
    @Test(priority = 0,description = "Fourth demo testcase", groups = {"FAS-113", "Login", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-113")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the 4th demo testcase")
    @Owner("Foden Duong")
    public void fourthTestcase(){
        Assert.assertTrue(true);
    }

    @Feature("Log in - Log out")
    @Test(priority = 0,description = "Fifth demo testcase", groups = {"FAS-114", "Login", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-114")
    @Severity(SeverityLevel.NORMAL)
    @Description("This is the 5th demo testcase")
    @Owner("Foden Duong")
    public void fifthTestcase(){
        Assert.assertTrue(true);
    }

}
