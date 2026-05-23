package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import br.ifsp.demo.ui.base.HomePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;

public class AuthenticationPageObject extends BasePageObject {
    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE = By.cssSelector(".error");
    private static final By REGISTER_LINK = By.linkText("Register");


    public AuthenticationPageObject(WebDriver driver) {
        super(driver);
        if (!Objects.requireNonNull(driver.getCurrentUrl()).contains("/login")) {
            throw new IllegalStateException("Not on login page");
        }
    }

    public HomePageObject authenticate(String email, String password) {
        type(USERNAME_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(SUBMIT_BUTTON);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("https://subscription-module-seven.vercel.app/"));

        return new HomePageObject(driver);
    }

    public RegistrationPageObject goToRegister() {
        click(REGISTER_LINK);
        return new RegistrationPageObject(driver);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public String getEmailValue() {
        return waitForElement(USERNAME_INPUT).getAttribute("value");
    }

    public String getPasswordValue() {
        return waitForElement(PASSWORD_INPUT).getAttribute("value");
    }

}
