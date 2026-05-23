package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.RegistrationPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class RegistrationPageObjectTest extends BaseSeleniumTest {
    private static final String URL = "https://subscription-module-seven.vercel.app/register";

    @Override
    public void setInitialPage() {
        driver.get(URL);
    }

    @Test
    @DisplayName("Should Registration a new profile")
    void shouldRegistrationANewProfile() {
        var registerPage = new RegistrationPageObject(driver);

        String email = STR."teste\{System.currentTimeMillis()}@teste.com";
        registerPage.register("teste1", "teste1", email, "teste1");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should not allow duplicated email registration")
    void shouldNotAllowDuplicatedEmailRegistration() {
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        var registerPage = new RegistrationPageObject(driver);

        registerPage.register("teste","teste",email,"teste123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        driver.get("https://subscription-module-seven.vercel.app/register");

        registerPage.register("teste", "teste", email,"teste123");

        assertThat(registerPage.pageErrorMessage()).contains("Could not register user.");
    }

}