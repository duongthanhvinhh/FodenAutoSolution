package org.foden.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{

    @FindBy(xpath = "//span[text()='My Account']")
    private WebElement myAccountDropdown;

    @FindBy(xpath = "//a[@href='https://tutorialsninja.com/demo/index.php?route=account/register']")
    private WebElement registerLink;

    @FindBy(xpath = "//a[@href='https://tutorialsninja.com/demo/index.php?route=account/login']")
    private WebElement loginLink;

    @FindBy(xpath = "//input[@id='input-email']")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@id='input-password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[@value='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//a[@class='list-group-item'][normalize-space()='Logout']")
    private WebElement logoutButtonAtBottomRight;

    @FindBy(xpath = "//div[@class='alert alert-danger alert-dismissible' and text()='Warning: No match for E-Mail Address and/or Password.']")
    private WebElement errorLoginMsg;

    public static ThreadLocal<LoginPage> loginPage = new ThreadLocal<>();

    public LoginPage(){
        init();
    }

    public static LoginPage getInstance(){
        try {
            if (loginPage.get() == null){
                loginPage.set(new LoginPage());
            }
            return loginPage.get();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return loginPage.get();
    }

    @Step("Go to Register page of QA Fox Application")
    public void goToRegisterPage() {
        clickToElement(myAccountDropdown);
        clickToElement(registerLink);
    }

    @Step("Go to Login page of QA Fox Application")
    public void goToLoginPage() {
        clickToElement(myAccountDropdown);
        clickToElement(loginLink);
    }

    @Step("Login QA Fox Application with gmail {0} and password {1}")
    public void login(String username, String password) {
        sendTextToElement(emailInput, username);
        sendTextToElement(passwordInput, password);
        click(loginButton, true);
    }

    @Step("Verify login successfully")
    public boolean verifyLoginSuccessfully(String partialUrl) {
        return isUrlContains(partialUrl) && checkForElementVisibility(logoutButtonAtBottomRight);
    }

    @Step("Verify login failed with message {0}")
    public boolean verifyLoginFailed() {
        return checkForElementVisibility(errorLoginMsg);
    }
}
