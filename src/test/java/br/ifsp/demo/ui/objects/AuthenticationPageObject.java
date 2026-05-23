package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
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

    public AuthenticationPageObject(WebDriver driver) {
        super(driver);
        if (!Objects.requireNonNull(driver.getCurrentUrl()).contains("/login")) {
            throw new IllegalStateException("Not on login page");
        }
    }
    public void authenticate(String email, String password){
        driver.findElement(By.name("username")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public RegistrationPageObject navigateToRegistrationPage(){
        driver.findElement(By.linkText("Register")).click();
        return new RegistrationPageObject(driver);
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

    public String email(){
        return driver.findElement(By.name("username")).getAttribute("value");
    }

    public String password(){
        return driver.findElement(By.id("password")).getText();
    }

}
