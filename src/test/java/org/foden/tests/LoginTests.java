package org.foden.tests;

import io.qameta.allure.*;
import org.foden.driver.BaseSteps;
import org.foden.enums.TestGroups;
import org.foden.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners({org.foden.listeners.AllureListener.class})
public class LoginTests extends BaseSteps {

    public LoginTests(){}

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify logging into the Application using valid credentials", groups = {"FAS-44", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-44")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify logging into the Application using valid credentials")
    @Owner("Foden Duong")
    public void loginSuccessfullyWithValidCredentials(){
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        loginPage.login("fodend1706@gmail.com", "Password@01");
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
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        loginPage.login("fodend1706@gmail.com", "WrongPassword");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed("Warning: No match for E-Mail Address and/or Password."),"Expected login but there's no message indicate that the login attempt was failed.");
        loginPage.login("test@gmail.com","Password@01");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed("Warning: No match for E-Mail Address and/or Password."),"Expected login but there's no message indicate that the login attempt was failed.");
        loginPage.login("foden1706@gmail.com", "");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed("Warning: No match for E-Mail Address and/or Password."),"Expected login but there's no message indicate that the login attempt was failed.");
        loginPage.login("","Password@01");
        softAssert.assertTrue(LoginPage.getInstance().verifyLoginFailed("Warning: No match for E-Mail Address and/or Password."),"Expected login but there's no message indicate that the login attempt was failed.");
        softAssert.assertAll();
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify 'Forgotten Password' link is available in the Login page and is working", groups = {"FAS-47", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-47")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify 'Forgotten Password' link is available in the Login page and is working")
    @Owner("Foden Duong")
    public void verifyForgottenPasswordFunction(){
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        loginPage.goToForgottenPasswordPage();
        loginPage.sentEmailConfirmationForNewPassword("test@gmail.com");
        Assert.assertTrue(loginPage.verifyEmailSentToUser());
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify E-Mail Address and Password text fields in the Login page have the place holder text ", groups = {"FAS-48", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-48")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify E-Mail Address and Password text fields in the Login page have the place holder text ")
    @Owner("Foden Duong")
    public void verifyPlaceHolderOfUserAndPasswordFields(){
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        Assert.assertTrue(loginPage.verifyPlaceholderOfEmailAndPasswordFields());
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify the different ways of navigating to the Login page", groups = {"FAS-52", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-52")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the different ways of navigating to the Login page")
    @Owner("Foden Duong")
    public void verifyAllWaysToNagigateToLoginPage(){
        SoftAssert softAssert = new SoftAssert();
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        softAssert.assertTrue(loginPage.verifyCanNavigateToLoginPageByClickingLoginLinkUnderRegisterPage(),"Can not go to login page by clicking login link under register page.");
        softAssert.assertTrue(loginPage.verifyCanNavigateToLoginPageByClickingLoginLinkInSidebar(),"Cannot go to login page by clicking login link in sidebar on the right");
        softAssert.assertAll();
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify the number of unsucessful login attemps", groups = {"FAS-59", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-59")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the number of unsucessful login attemps")
    @Owner("Foden Duong")
    public void verifyTheNumberOfUnsuccessfulLoginAttempts(){
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        loginPage.login("foden1707@gmail.com","wrongpwd");
        loginPage.login("foden1707@gmail.com","wrongpwd");
        loginPage.login("foden1707@gmail.com","wrongpwd");
        loginPage.login("foden1707@gmail.com","wrongpwd");
        loginPage.login("foden1707@gmail.com","wrongpwd");
        loginPage.login("foden1707@gmail.com","wrongpwd");
        Assert.assertTrue(loginPage.unsuccessfulLoginAtemptsDisplayedAfterFiveAttempts(),"Login attempts exceed was not displayed after 5 attempts with wrong password.");
    }

    @Epic("FAS-7: Feature: Authentication - Authorization")
    @Feature("FAS-11: Develope scripts for scenarios Login, Logout")
    @Test(priority = 0,description = "Verify the Breakcrumb, Page Heading, Page Title and Page URL of Login page", groups = {"FAS-53", "FAS-11", TestGroups.Id.SMOKE, TestGroups.Id.REGRESSION})
    @Story("FAS-53")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the Breakcrumb, Page Heading, Page Title and Page URL of Login page")
    @Owner("Foden Duong")
    public void verifyUiOfLoginPage(){
        SoftAssert softAssert = new SoftAssert();
        initializeWebDriver();
        LoginPage loginPage = LoginPage.getInstance();
        loginPage.goToLoginPage();
        softAssert.assertEquals(loginPage.getLoginPageTitle(),"Account Login","Page title not matched.");
        softAssert.assertTrue(loginPage.getLoginPageUrl().contains("route=account/login"),"Page Url not matched.");
        softAssert.assertTrue(loginPage.pageHeadingIsDisplayedCorrectly(),"Page Header not displayed.");
        softAssert.assertTrue(loginPage.verifyBreadCrumb(),"Breadcrumb is not displayed correctly.");
        softAssert.assertAll();
    }
}
