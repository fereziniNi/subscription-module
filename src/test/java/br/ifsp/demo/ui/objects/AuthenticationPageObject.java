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

public class AuthenticationPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "frontend";

    public AuthenticationPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".error")
                )
        );
        return errorMessage.getText();
    }

    public String email(){
        return driver.findElement(By.id("username")).getText();
    }

    public String password(){
        return driver.findElement(By.id("password")).getText();
    }

}
