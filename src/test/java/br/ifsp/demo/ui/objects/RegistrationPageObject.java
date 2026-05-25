package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class RegistrationPageObject extends BasePageObject {
    private static final By PAGE_TITLE = By.cssSelector(".page-title");
    private static final By PAGE_SUBTITLE = By.cssSelector(".page-subtitle");
    private static final By NAME_INPUT = By.name("name");
    private static final By LASTNAME_INPUT = By.name("lastname");
    private static final By EMAIL_INPUT = By.name("email");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By LOGIN_LINK = By.cssSelector("a[href='/login']");
    private static final By BACK_BUTTON = By.cssSelector("button.secondary[type='button']");

    public RegistrationPageObject(WebDriver driver) {
        super(driver);
        if (!Objects.requireNonNull(driver.getCurrentUrl()).contains("/register")) {
            throw new IllegalStateException("Not on register page");
        }
    }

    public boolean isNameFieldVisible() {
        return driver.findElement(By.name("name")).isDisplayed();
    }

    public boolean isLastNameFieldVisible() {
        return driver.findElement(By.name("lastname")).isDisplayed();
    }

    public boolean isEmailFieldVisible() {
        return driver.findElement(By.name("email")).isDisplayed();
    }

    public boolean isPasswordFieldVisible() {
        return driver.findElement(By.name("password")).isDisplayed();
    }

    public void register(String name, String lastname, String email, String password){
        driver.findElement(By.name("name")).sendKeys(name);
        driver.findElement(By.name("lastname")).sendKeys(lastname);
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public String pageErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".error")
                )
        );
        return errorMessage.getText();
    }

    public boolean isCreateAccountButtonVisible() {
        return driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed();
    }

    public boolean isLoginLinkVisible() {
        return driver.findElement(By.cssSelector("a[href='/login']")).isDisplayed();
    }

    public String getPageTitle() {
        return driver.findElement(PAGE_TITLE).getText();
    }

    public String getPageSubtitle() {
        return driver.findElement(PAGE_SUBTITLE).getText();
    }

    public String getNamePlaceholder() {
        return driver.findElement(NAME_INPUT).getAttribute("placeholder");
    }

    public String getLastnamePlaceholder() {
        return driver.findElement(LASTNAME_INPUT).getAttribute("placeholder");
    }

    public String getEmailPlaceholder() {
        return driver.findElement(EMAIL_INPUT).getAttribute("placeholder");
    }

    public String getPasswordPlaceholder() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("placeholder");
    }

    public String getSubmitButtonText() {
        return driver.findElement(SUBMIT_BUTTON).getText();
    }

    public boolean isBackButtonVisible() {
        return driver.findElement(BACK_BUTTON).isDisplayed();
    }

    public String getBackButtonText() {
        return driver.findElement(BACK_BUTTON).getText();
    }

    public String getNameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String getLastnameValue() {
        return driver.findElement(LASTNAME_INPUT).getAttribute("value");
    }

    public String getEmailValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }
    public String getPasswordValue() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("value");
    }

    public void clickLoginLink() {
        driver.findElement(LOGIN_LINK).click();
    }

    public void clickBackButton() {
        driver.findElement(BACK_BUTTON).click();
    }

    public boolean hasErrorMessage() {
        try {
            driver.findElement(By.cssSelector(".error"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillLastname(String lastname) {
        driver.findElement(LASTNAME_INPUT).clear();
        if (lastname != null && !lastname.isEmpty()) {
            driver.findElement(LASTNAME_INPUT).sendKeys(lastname);
        }
    }

    public void fillEmail(String email) {
        driver.findElement(EMAIL_INPUT).clear();
        if (email != null && !email.isEmpty()) {
            driver.findElement(EMAIL_INPUT).sendKeys(email);
        }
    }

    public void fillPassword(String password) {
        driver.findElement(PASSWORD_INPUT).clear();
        if (password != null && !password.isEmpty()) {
            driver.findElement(PASSWORD_INPUT).sendKeys(password);
        }
    }

    public void clickSubmitButton() {
        driver.findElement(SUBMIT_BUTTON).click();
    }
}