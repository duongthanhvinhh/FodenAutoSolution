package org.foden.tests;

import io.qameta.allure.*;
import org.foden.enums.TestGroups;
import org.foden.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners({org.foden.listeners.AllureListener.class})
public class LoginTest extends BaseTest{

    private LoginTest(){}

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify logging into the Application using valid credentials", groups = {"FAS-44", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-44")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify logging into the Application using valid credentials")
    @Owner("Foden Duong")
    public void loginSuccessfullyWithValidCredentials(){
        LoginPage.getInstance().goToLoginPage();
        LoginPage.getInstance().login("fodend1706@gmail.com", "Password@01");
        Assert.assertTrue(LoginPage.getInstance().verifyLoginSuccessfully("route=account/account"),"Login failed");
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify logging into the Application using invalid credentials", groups = {"FAS-45", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-45")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify logging into the Application using invalid credentials")
    @Owner("Foden Duong")
    public void loginFailedWithInvalidCredentials(){
        SoftAssert softAssert = new SoftAssert();
        LoginPage.getInstance().goToLoginPage();
        LoginPage.getInstance().login("fodend1706@gmail.com", "WrongPassword");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed(),"Expected login but there's no message indicate that the login attempt was failed.");
        LoginPage.getInstance().login("test@gmail.com","Password@01");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed(),"Expected login but there's no message indicate that the login attempt was failed.");
        LoginPage.getInstance().login("foden1706@gmail.com", "");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed(),"Expected login but there's no message indicate that the login attempt was failed.");
        LoginPage.getInstance().login("","Password@01");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed(),"Expected login but there's no message indicate that the login attempt was failed.");
        softAssert.assertAll();
    }
}
