package br.ifsp.demo.ui.test;

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

}