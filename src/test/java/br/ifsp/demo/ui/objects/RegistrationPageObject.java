package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegistrationPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "frontend";

    public RegistrationPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle()))
            throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
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

    public boolean isCreateAccountButtonVisible() {
        return driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed();
    }

    public boolean isLoginLinkVisible() {
        return driver.findElement(By.cssSelector("a[href='/login']")).isDisplayed();
    }

}