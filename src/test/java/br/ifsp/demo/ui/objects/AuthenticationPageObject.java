package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Objects;

public class AuthenticationPageObject extends BasePageObject {
    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE = By.cssSelector(".error");
    private static final By REGISTER_LINK = By.linkText("Register");
    private static final By PAGE_TITLE = By.cssSelector(".page-title");
    private static final By PAGE_SUBTITLE = By.cssSelector(".page-subtitle");


    public AuthenticationPageObject(WebDriver driver) {
        super(driver);
        if (!Objects.requireNonNull(driver.getCurrentUrl()).contains("/login")) {
            throw new IllegalStateException("Not on login page");
        }
    }

    public HomePageObject loginSuccessfully(String email, String password) {
        type(USERNAME_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(SUBMIT_BUTTON);
        wait.until(ExpectedConditions.urlToBe("https://subscription-module-seven.vercel.app/"));
        return new HomePageObject(driver);
    }

    public void attemptLogin(String email, String password) {
        type(USERNAME_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(SUBMIT_BUTTON);
    }

    public RegistrationPageObject goToRegister() {
        click(REGISTER_LINK);
        return new RegistrationPageObject(driver);
    }

    public String getErrorMessage() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)
        ).getText();
    }

    public String getEmailValue() {
        return waitForElement(USERNAME_INPUT).getAttribute("value");
    }

    public String getPasswordValue() {
        return waitForElement(PASSWORD_INPUT).getAttribute("value");
    }

    public String getPasswordType() {
        return waitForElement(PASSWORD_INPUT).getAttribute("type");
    }

    public boolean isPageTitleVisible() {
        return isElementPresent(PAGE_TITLE);
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public String getPageSubtitle() {
        return getText(PAGE_SUBTITLE);
    }

    public boolean isEmailFieldVisible() {
        return isElementPresent(USERNAME_INPUT);
    }

    public boolean isPasswordFieldVisible() {
        return isElementPresent(PASSWORD_INPUT);
    }

    public String getEmailPlaceholder() {
        return waitForElement(USERNAME_INPUT).getAttribute("placeholder");
    }

    public String getPasswordPlaceholder() {
        return waitForElement(PASSWORD_INPUT).getAttribute("placeholder");
    }

    public boolean isSubmitButtonVisible() {
        return isElementPresent(SUBMIT_BUTTON);
    }

    public String getSubmitButtonText() {
        return getText(SUBMIT_BUTTON);
    }

    public boolean isRegisterLinkVisible() {
        return isElementPresent(REGISTER_LINK);
    }

    public String getRegisterLinkText() {
        return getText(REGISTER_LINK);
    }
}
