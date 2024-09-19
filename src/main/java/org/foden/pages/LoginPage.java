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

    @FindBy(xpath = "//a[normalize-space()='login page']")
    private WebElement loginLinkUnderRegisterPage;

    @FindBy(xpath = "//a[@class='list-group-item'][normalize-space()='Login']")
    private WebElement loginLinkRightSideBar;

    @FindBy(xpath = "//input[@id='input-email']")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@placeholder='E-Mail Address']")
    private WebElement placeholderEmailInput;

    @FindBy(xpath = "//input[@id='input-password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[@placeholder='Password']")
    private WebElement placeholderPasswordInput;

    @FindBy(xpath = "//input[@value='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//a[@class='list-group-item'][normalize-space()='Logout']")
    private WebElement logoutButtonAtBottomRight;

    @FindBy(xpath = "//div[@class='alert alert-danger alert-dismissible' and text()='Warning: No match for E-Mail Address and/or Password.']")
    private WebElement errorLoginMsg;
    
    @FindBy(xpath = "//div[@class='form-group']//a[normalize-space()='Forgotten Password']")
    private WebElement forgottenPasswordLink;

    @FindBy(xpath = "//h1[normalize-space()='Forgot Your Password?']")
    private WebElement forgottenPasswordHeaderPage;

    @FindBy(xpath = "//input[@value='Continue']")
    private WebElement continueButton;

    @FindBy(xpath = "//div[text()='An email with a confirmation link has been sent your email address.']")
    private WebElement confirmEmailAlertSuccess;
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

    @Step("Go to Forgotten Password page")
    public void goToForgottenPasswordPage() {
        clickToElement(forgottenPasswordLink);
    }

    @Step("Go to Forgotten Password page")
    public void sentEmailConfirmationForNewPassword(String email) {
        waitUntilLoadedAndElementVisible(forgottenPasswordHeaderPage);
        sendTextToElement(emailInput,email);
        clickToElement(continueButton);
    }

    @Step("Verify email sent to user for setting up the new password")
    public boolean verifyEmailSentToUser() {
        return checkForElementVisibility(confirmEmailAlertSuccess);
    }

    @Step("Verify placeholder of email and password input fields")
    public boolean verifyPlaceholderOfEmailAndPasswordFields() {
        return checkForElementVisibility(placeholderPasswordInput) && checkForElementVisibility(placeholderPasswordInput);
    }

    @Step("Verify can go to login page by click login link under Register page")
    public boolean verifyCanNavigateToLoginPageByClickingLoginLinkUnderRegisterPage() {
        goToRegisterPage();
        clickToElement(loginLinkUnderRegisterPage);
        return checkForElementVisibility(loginButton);
    }

    @Step("Verify can go to login page by click login link in Sidebar on the right")
    public boolean verifyCanNavigateToLoginPageByClickingLoginLinkInSidebar() {
        goToRegisterPage();
        clickToElement(loginLinkRightSideBar);
        return checkForElementVisibility(loginButton);
    }


}
